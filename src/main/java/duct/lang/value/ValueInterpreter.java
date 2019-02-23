package duct.lang.value;

import duct.lang.ParseUtils;
import duct.lang.value.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

class ValueInterpreter {
	private static final String UNKNOWN_TYPE_ERR_MSG           = "Cannot interpret value with type given.";
	private static final String SRC_READ_ERROR_MSG             = "Unable to read source";
	private static final String VALUE_NOT_ENCLOSED_MSG         = "A value must open with '<' and close with '>'";
	private static final String IDENTIFIER_ORDER_ERR_MSG       = "The name or identifier of a value must be specified before it's type is specified.";
	private static final String TYPE_ALREADY_SPECIFIED_ERR_MSG = "The type has already been specified for this value.";
	private static final String TYPE_UNSPECIFIED_ERR_MSG       = "A type must be specified for each value.";

	private static final String TYPE_INCORRECTLY_SPECIFIED_ERR_MSG = "The type must be specified before it's value.";

	public ValueInterpreter(){
	}

	static Value parseNextValue( Reader reader ) throws ParseException, IOException {
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
		} else if( type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer) ){
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

		if( type.isContainer ){
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
		} else if(type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer)){
			throw new ParseException(TYPE_ALREADY_SPECIFIED_ERR_MSG, state.getStartOfValue() );
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

	static Object interpretValue( Type valueType, CharSequence value ) throws ParseException {

		switch( valueType ){
			case TEXT:
				return ValueInterpreter.interpretString( value );
			case NUMBER:
				return ValueInterpreter.interpretNumber( value );
			case BOOL:
				return ValueInterpreter.interpretBool(   value );
			case MODULE:
				return ValueInterpreter.interpretModule( value );
			case LIST:
				return ValueInterpreter.interpretList(   value );
			case SET:
				return ValueInterpreter.interpretSet(    value );
		}

		throw new ParseException( ValueInterpreter.UNKNOWN_TYPE_ERR_MSG, 0);
	}

	private static String interpretString( CharSequence value ){
		return StringUtils.remove(value.toString(), '\\');
	}

	/**
	 * Interprets the string value as a number. It first tries to interpret as a long and then as a double.
	 **/
	private static Number interpretNumber( CharSequence value ) throws ValueInitException {
		try{
			return Long.parseLong( value.toString() );
		} catch( NumberFormatException nfe ){
			try{
				return Double.parseDouble( value.toString() );
			} catch( NumberFormatException nfe2 ){
				throw new ValueInitException( "Unable to interpret number from given text '" + value + "'.", 0 );
			}
		}
	}

	private static List<CharSequence> boolStringValues = Arrays.asList( "true", "false" );

	/**
	 * Interprets the string value as a boolean.
	 * @return If the value is either 'true' or 'false' it returns that value else,
	 *		an exception is thrown.
	 **/
	private static Boolean interpretBool( CharSequence value ) throws ValueInitException {
		value = StringUtils.trimToEmpty( value.toString() ).toLowerCase();

		if( !boolStringValues.contains( value ) ) {
			throw new ValueInitException( "Value given '" + value + "' is not a boolean value.", 0 );
		}

		return Boolean.parseBoolean( value.toString() );
	}

	static String interpretModule( CharSequence value ){
		return value.toString();
	}

	/*
	 * Interprets the char sequence value as a list of Duct Values. Values are accessed from a list by calling it as a function with the index passed as a parameter.
	 * @return A list a Duct values if one exists
	 */
	private static List<Value> interpretList( CharSequence value ) throws ParseException {
		List<Value> listValue = new ArrayList<>();
		try {
			StringReader reader = new StringReader( value.toString() );

			Value nextValue;
			do{
				nextValue = ValueInterpreter.parseNextValue( reader );
				if( nextValue != null ){
					listValue.add( nextValue );
				}
			} while( nextValue != null );
		} catch( IOException i ){
			throw new ParseException( SRC_READ_ERROR_MSG, 0 );
		}

		return listValue;
	}

	/*
	 * Interprets the char sequence value as a set of named duct values. Accessing values from a set is similar to accessing values from a list, only rather than an index, a name is given.
	 * @return A set of named duct values if one exists
	 */
	private static Map<String, Value> interpretSet( CharSequence value ) throws ParseException {
		Map<String,Value> set = new HashMap<>();

		//the result of 'interpretList' will always be a super set of the result of 'interpretSet'
		List<Value> valueList = interpretList( value );

		//if the value has no name then the value can't be accessed so there is no point in adding it to the set.
		for( Value d:valueList ){
			if( !StringUtils.isEmpty(d.name) ){
				set.put( d.name, d );
			}
		}

		return set;
	}
}