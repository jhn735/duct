package duct.lang.value;

import java.io.Reader;
import java.io.IOException;
import java.lang.Character;
import java.lang.CharSequence;
import java.lang.StringBuilder;
import java.lang.Exception;
import java.text.ParseException;
import duct.lang.Element;
import duct.lang.Evaluable;
import duct.lang.ParseUtils;

public class Value extends Element implements Evaluable {

	private static final String VALUE_NOT_ENCLOSED_MSG         = "A value must open with '<' and close with '>'";
	private static final String IDENTIFIER_ORDER_ERR_MSG       = "The name or identifier of a value must be specified before it's type is specified.";
	private static final String TYPE_ALREADY_SPECIFIED_ERR_MSG = "The type has already been specified for this value.";
	private static final String TYPE_UNSPECIFIED_ERR_MSG       = "The type must be specified before it's value.";

	public final Type type;
	protected Object value;

	public Object getValue(){
		return this.value;
	}

	protected Value(){
		super( "" );
		this.type  = Type.TEXT;
		this.value = null;
	}

	public Value( Type valueType, CharSequence name, CharSequence value ) throws ParseException {
		super( constructName( valueType, name, value ) );
		this.type = valueType;
		this.value = ValueInterpreter.interpretValue( valueType, value );
	}

	public Value( Value d ){
		super( d.name );
		this.type  = d.type;
		this.value = d.getValue();
	}

	public Value evaluate(){
		return this;
	}

	private static String constructName( Type t, CharSequence name, CharSequence value ){
		String properName = (name == null) ? "" : name.toString();

		if( t == Type.MODULE && properName.isEmpty() ){
			properName = ValueInterpreter.interpretModule( value );
		}

	return properName;
	}

	public String toString(){
		return value.toString();
	}

	public boolean equals( Value obj ) {
		return obj.type == this.type && this.value.equals(obj.getValue());
	}

	public static Value defaultValue( Type valueType ){
		try {
			switch( valueType ){
				case NUMBER: return new Value( valueType, "", "0" );
				case BOOL:   return new Value( valueType, "", "false" );
				default:     return new Value( valueType, "", "" );
			}
		} catch( Exception e ){
			return null;
		}
	}

	public static Value nextValue( Reader reader ) throws ParseException, IOException {
		StringBuilder extractedValue = new StringBuilder();
		//make sure to get number of characters read, in case a parse exception is thrown.
		int charCount = 0;
		char curChar;

		do {
			curChar = ParseUtils.readNextChar( reader );
			charCount++;
		} while( Character.isWhitespace( curChar ) );

		//throw a fit if the value is not started properly
		if( curChar != '<' ){
			throw new ParseException( VALUE_NOT_ENCLOSED_MSG, charCount );
		}

		int valueStart = charCount;

		//working on the assumption that the first '<' is there. otherwise an exception would have been thrown at this point.
		long enclosureDepth = 1;
		Type   type = null;
		String name = null;

		do{
			curChar = ParseUtils.readNextChar(reader);
			charCount++;
			//the reason I have each case adding the current character to the extracted value rather than having that code after the switch statement is to simplify
			//the control flow. No need to make assumptions about what should occur outside of the code within that case statement. Each is responsible to wrap things up.
			switch( curChar ){
				//this indicates that the value is named something. The name of the value can only be specified before the type is specified, hence the null check.
				case '#':
					if( type == null ){
						name = extractedValue.toString();
						extractedValue.setLength(0);
					//if the the type is text then we can assume that the '#' is a part of the text value. Otherwise an error has occurred.
					//also if the type is a container and are currently reading a contained value, it will be parsed later on it's own merit anyway.
					} else if( type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer) ){
						throw new ParseException( IDENTIFIER_ORDER_ERR_MSG, valueStart );
					} else {
						extractedValue.append( curChar );
					}
				break;

				//we only need to worry about matching pairs of '<' and '>' in lists, sets and scripts.
				case '<':
					if( type == null ){
						throw new ParseException( Value.TYPE_UNSPECIFIED_ERR_MSG, valueStart );
					}

					if( type.isContainer ){
						enclosureDepth++;
					}

					extractedValue.append( curChar );
				break;

				case '>':
					enclosureDepth--;
					if( enclosureDepth > 0 ){
						extractedValue.append( curChar );
					}
				break;

				case ':':
					//if the type has not been extracted, try to parse the type with the characters gathered and 'clear' them.
					if( type == null ){
						try{
						 type = Type.parseType( extractedValue );
						}catch( ParseException p ){
						 throw new ParseException( p.getMessage(), p.getErrorOffset() + valueStart );
						}
						
						extractedValue.setLength(0);
						valueStart = charCount+1;
					//otherwise throw an error if the type has already been specified.
					} else if(type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer)){
						throw new ParseException(TYPE_ALREADY_SPECIFIED_ERR_MSG, valueStart);
					//finally if nothing else is going on, add the current character to the extracted value.
					} else {
						extractedValue.append( curChar );
					}
				break;
				
				case '\\':
					extractedValue.append( curChar );
					curChar = ParseUtils.readNextChar(reader);
					charCount++;
					extractedValue.append( curChar );
				break;
				
				default:
					extractedValue.append( curChar );
			}
		} while( enclosureDepth > 0 );
		
		if( type == null ){
			throw new ParseException( "A type must be specified for each value.", valueStart );
		}

		//Attempt to parse the value having parsed the type and extracted the name and the string value.
		Value d = new Value( type, name, extractedValue );
	return d;
	}
}
