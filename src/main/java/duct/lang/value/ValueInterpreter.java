package duct.lang.value;

import duct.lang.ParseUtils;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public class ValueInterpreter {
	        static final String UNKNOWN_TYPE_ERR_MSG           = "Cannot interpret value with type given.";
	private static final String VALUE_NOT_ENCLOSED_MSG         = "A value must open with '<' and close with '>'";
	private static final String IDENTIFIER_ORDER_ERR_MSG       = "The name or identifier of a value must be specified before it's type is specified.";
	private static final String TYPE_ALREADY_SPECIFIED_ERR_MSG = "The type has already been specified for this value.";
	private static final String TYPE_UNSPECIFIED_ERR_MSG       = "A type must be specified for each value.";

	private static final String TYPE_INCORRECTLY_SPECIFIED_ERR_MSG = "The type must be specified before it's value.";

	public ValueInterpreter(){}

	public static Value parseNextValue( Reader reader ) throws ParseException, IOException {
		ValueInterpreterState interpreterState = new ValueInterpreterState( reader );

		try{
			interpreterState.readNextCharacterSkippingWhitespaceFromReader();
		} catch( ParseUtils.PrematureEndOfStreamException p ){
			return null;
		}

		//throw a fit if the value is not started properly
		if( interpreterState.getCurrentCharacter() != '<' ){
			throw new ParseException( VALUE_NOT_ENCLOSED_MSG, interpreterState.getCharacterCount() );
		}

		interpreterState.setStartOfValue( interpreterState.getCharacterCount() );

		//working on the assumption that the first '<' is there. otherwise an exception would have been thrown at this point.
		interpreterState.increaseEnclosureDepth();

		while( interpreterState.isEnclosureDepthNonZero() ){
			interpreterState.readNextCharacterFromReader();
			//the reason I have each case adding the current character to the extracted value rather than having that code after the switch statement is to simplify
			//the control flow. No need to make assumptions about what should occur outside of the code within that case statement. Each is responsible to wrap things up.
			switch( interpreterState.getCurrentCharacter() ){
				//this indicates that the value is named something. The name of the value can only be specified before the type is specified, hence the null check.
				case '#':
					handleNamedValueIndicator( interpreterState );
				break;
				//we only need to worry about matching pairs of '<' and '>' in lists, sets and scripts.
				case '<':
					handleOpenEnclosure( interpreterState );
				break;
				case '>':
					handleCloseEnclosure( interpreterState );
				break;
				case ':':
					handleValueTypeIndicator( interpreterState);
				break;
				case '\\':
					handleEscapeSequence( interpreterState );
				break;
				default:
					interpreterState.addCurrentCharacterToExtractedValue();
			}
		}

		if( interpreterState.getTypeOfValue() == null ){
			throw new ParseException( ValueInterpreter.TYPE_UNSPECIFIED_ERR_MSG, interpreterState.getStartOfValue() );
		}

		//Attempt to parse the value having parsed the type and extracted the name and the string value.
		return interpreterState.toValue();
	}

	private static void handleNamedValueIndicator( ValueInterpreterState state ) throws ParseException {
		Type type = state.getTypeOfValue();
		long enclosureDepth = state.getEnclosureDepth();
		if( type == null ){
			state.extractNameFromExtractedValue();
		//if the the type is text then we can assume that the '#' is a part of the text value. Otherwise an error has occurred.
		//also if the type is a container and are currently reading a contained value, it will be parsed later on it's own merit anyway.
		} else if( type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer()) ){
			throw new ParseException( IDENTIFIER_ORDER_ERR_MSG, state.getStartOfValue() );
		} else {
			state.addCurrentCharacterToExtractedValue();
		}
	}

	private static void handleOpenEnclosure( ValueInterpreterState state ) throws ParseException {
		Type type = state.getTypeOfValue();
		if( type == null ){
			throw new ParseException( ValueInterpreter.TYPE_INCORRECTLY_SPECIFIED_ERR_MSG, state.getStartOfValue() );
		}

		if( type.isContainer() ){
			state.increaseEnclosureDepth();
		}

		state.addCurrentCharacterToExtractedValue();
	}

	private static void handleCloseEnclosure( ValueInterpreterState state ){
		state.decreaseEnclosureDepth();
		if( state.getEnclosureDepth() > 0 ){
			state.addCurrentCharacterToExtractedValue();
		}
	}

	private static void handleValueTypeIndicator( ValueInterpreterState state ) throws ParseException {
		Type type = state.getTypeOfValue();
		long enclosureDepth = state.getEnclosureDepth();
		//if the type has not been extracted, try to parse the type with the characters gathered and 'clear' them.
		if( type == null ){
			try{
				state.extractTypeFromExtractedValue();
			}catch( ParseException p ){
				throw new ParseException( p.getMessage(), p.getErrorOffset() + state.getStartOfValue() );
			}

			state.setStartOfValue( state.getCharacterCount() + 1 );
		//otherwise throw an error if the type has already been specified.
		} else if( type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer()) ){
			throw new ParseException( ValueInterpreter.TYPE_ALREADY_SPECIFIED_ERR_MSG, state.getStartOfValue() );
			//finally if nothing else is going on, add the current character to the extracted value.
		} else {
			state.addCurrentCharacterToExtractedValue();
		}

	}

	private static void handleEscapeSequence( ValueInterpreterState state ) throws IOException, ParseException {
		state.addCurrentCharacterToExtractedValue();
		state.readNextCharacterFromReader();
		state.addCurrentCharacterToExtractedValue();
	}
}