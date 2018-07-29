package duct.main.lang;

import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;

import java.lang.Character;
import java.lang.CharSequence;
import java.lang.Double;
import java.lang.Long;
import java.lang.Number;
import java.lang.StringBuilder;
import java.lang.Exception;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;

public class Value extends Element implements Evaluable {

	public static class ValueInitException extends ParseException{
		public static final String BASE_MSG = "Error occurred while initalizing value: ";
		public ValueInitException(String s, int errorOffset){ super(BASE_MSG + s, errorOffset); }
	}
	
	public final Type type;
	protected Object value;

	public Object getValue(){
		return this.value;
	}

	protected Value(){
		super("");
		this.type = Type.TEXT;
		this.value = null;
	}

	public Value(Type t, CharSequence name, CharSequence value) throws ParseException{
		super(constructName(t, name, value));
		this.type = t;

		switch(t){
			case TEXT:   this.value = interpretString(value); break;
			case NUMBER: this.value = interpretNumber(value); break;
			case BOOL:   this.value = interpretBool(value);   break;
			case MODULE: this.value = interpretModule(value); break;
			case LIST:   this.value = interpretList(value);   break;
			case SET:    this.value = interpretSet(value);    break;
			case SCRIPT: this.value = interpretScript(value); break;
			default:     throw new ParseException("Cannot interpret value with type given." , 0);
		}
	}

	public Value(Value d){
		super(d.name);
		this.type = d.type;
		this.value = d.getValue();
	}

	public Value evaluate(){
		return this;
	}

	public static String constructName(Type t, CharSequence name, CharSequence value){
		String properName = (name == null)?"":name.toString();
		try{
			if(t == Type.MODULE && properName.isEmpty())
				properName = interpretModule(value);
		} catch (ValueInitException v){
			return "";
		}
	
	return properName;
	}
	
	public String toString(){
		return value.toString();
	}
		
	public boolean equals(Value obj){
		return obj.type == this.type && this.value.equals(obj.getValue());
	}
	
	public static String interpretString(CharSequence value) {
		return StringUtils.remove(value.toString(), '\\');
	}

	public static Value defaultValue( Type t ){
		try {
			switch(t){
				case NUMBER: return new Value(t, "", "0");
				case BOOL:   return new Value(t, "", "false");
				default:     return new Value(t, "", "");
			}
		} catch( Exception e ) {
			return null;
		}
	}

	/**
	 * Interprets the string value as a number. It first tries to interpret as a long and then as a double.
	**/
	public static Number interpretNumber(CharSequence value) throws ValueInitException {
		Number n = null;
		try{
			n = Long.parseLong(value.toString());
		}catch(NumberFormatException nfe){
			try{
				n = Double.parseDouble(value.toString());
			}catch(NumberFormatException nfe2){
				throw new ValueInitException("Unable to interpret number from given text '"+value+"'.", 0);
			}
		}
	return n;
	}

	public static List<CharSequence> boolStringValues = Arrays.asList("true", "false");
	/**
	 * Interprets the string value as a boolean.
	 * @return If the value is either 'true' or 'false' it returns that value else,
	 *		an exception is thrown.
	**/
	public static Boolean interpretBool(CharSequence value) throws ValueInitException {
		value = StringUtils.trimToEmpty(value.toString()).toLowerCase();
		if(!boolStringValues.contains(value))
			throw new ValueInitException("Value given '" + value + "' is not a boolean value.", 0);

	return Boolean.parseBoolean(value.toString());
	}

	public static String interpretModule(CharSequence value) throws ValueInitException {
		return value.toString();
	}
	private static final String SRC_READ_ERROR_MSG = "Unabe to read source";

	/*
	 * Interprets the char sequence value as a list of Duct Values. Values are accessed from a list by calling it as a function with the index passed as a parameter.
	 * @return A list a Duct values if one exists
	*/
	public static List<Value> interpretList(CharSequence value) throws ValueInitException, ParseException{
		List<Value> listValue = new ArrayList<Value>();
		try {
			StringReader r = new StringReader( value.toString().trim() );
			r.mark(1);
			//while the reader isn't empty
			while(r.read() >= 0 ){
				r.reset();

				listValue.add(nextValue(r));
				r.mark(1);
			}
		} catch(IOException i){
			throw new ParseException( SRC_READ_ERROR_MSG, 0);
		}
		
	return listValue;
	}

	/*
	 * Interprets the char sequence value as a set of named duct values. Accessing values from a set is similar to accessing values from a list, only rather than an index, a name is given.
	 * @return A set of named duct values if one exists
	*/
	public static Map<String, Value> interpretSet(CharSequence value) throws ValueInitException, ParseException {
		Map<String,Value> set = new HashMap<String, Value>();

		//the result of 'interpretList' will always be a super set of the result of 'interpretSet'
		List<Value> valueList = interpretList(value);

	 //if the value has no name then the value can't be accessed so there is no point in adding it to the set.
		for(Value d:valueList)
			if(!StringUtils.isEmpty(d.name))
				set.put(d.name, d);

	return set;
	}

	private static final String VALUE_NOT_ENCLOSED_MSG = "A value must open with '<' and close with '>'";
	private static final String IDENTIFIER_ORDER_ERR_MSG = "The name or identifier of a value must be specified before it's type is specified.";
	private static final String TYPE_ALREADY_SPECIFIED_ERR_MSG = "The type has already been specified for this value.";
	public static Value nextValue(Reader reader) throws ParseException, IOException {
		StringBuilder extractedValue = new StringBuilder();
		//make sure to get number of charecters read, in case a parse exception is thrown.
		int charCount = 0;
		char curChar;

		do {
			curChar = ParseUtils.readNextChar(reader);
			charCount++;
		} while(Character.isWhitespace(curChar) );

		//throw a fit if the value is not started properly
		if(curChar != '<')
			throw new ParseException(VALUE_NOT_ENCLOSED_MSG, charCount);

		int valueStart = charCount;

		//working on the assumption that the first '<' is there. otherwise an exception would have been thrown at this point.
		long enclosureDepth = 1;
		Type type = null;
		String name = null;

		do{
			curChar = ParseUtils.readNextChar(reader);
			charCount++;
			//the reason I have each case adding the current character to the extracted value rather than having that code after the switch statement is to simplify
			//the control flow. No need to make assumptions about what should occur outside of the code within that case statement. Each is responsible to wrap things up.
			switch(curChar){
				//this indicates that the value is named something. The name of the value can only be specified before the type is specified, hence the null check.
				case '#':
					if(type == null){
						name = extractedValue.toString();
						extractedValue.setLength(0);
					//if the the type is text then we can assume that the '#' is a part of the text value. Otherwise an error has occurred.
					//also if the type is a container and are currently reading a contained value, it will be parsed later on it's own merit anyway. 
					} else if(type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer)) {
						throw new ParseException( IDENTIFIER_ORDER_ERR_MSG, valueStart);
					} else {
						extractedValue.append(curChar);
					}
				break;

				//we only need to worry about matching pairs of '<' and '>' in lists, sets and scripts.
				case '<':
					if(type.isContainer)
						enclosureDepth++;
					extractedValue.append(curChar);
				break;

				case '>':
					enclosureDepth--;
					if(enclosureDepth > 0)
						extractedValue.append(curChar);
				break;

				case ':':	
					//if the type has not been extracted, try to parse the type with the characters gathered and 'clear' them.
					if(type == null){
						try{
						 type = Type.parseType(extractedValue);
						}catch (ParseException p){
						 throw new ParseException(p.getMessage(), p.getErrorOffset() + valueStart);
						}
						
						extractedValue.setLength(0);
						valueStart = charCount+1;
					//otherwise throw an error if the type has already been specified.
					} else if(type != Type.TEXT && !(enclosureDepth > 1 && type.isContainer)){
						throw new ParseException(TYPE_ALREADY_SPECIFIED_ERR_MSG, valueStart);
					//finally if nothing else is going on, add the current character to the extracted value.
					} else {
						extractedValue.append(curChar);
					}
				break;
				
				case '\\':
					extractedValue.append(curChar);
					curChar = ParseUtils.readNextChar(reader);
					charCount++;
					extractedValue.append(curChar);
				break;
				
				default:
					extractedValue.append(curChar);
			}
		} while(enclosureDepth > 0);
		
		if(type == null)
			throw new ParseException("A type must be specified for each value.", valueStart);

		Value d = null;

		//Attempt to parse the value having parsed the type and extracted the name and the string value.	
		d = new Value(type, name, extractedValue);
	return d;
	}		
	
	/**
	 * Saves the value as a string, parsing later upon execution.
	 * @return The string value of the value given.
	**/
	public static String interpretScript(CharSequence value) throws ValueInitException{
		return value.toString();
	}
}
