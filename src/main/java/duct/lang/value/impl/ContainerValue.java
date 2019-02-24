package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import duct.lang.value.ValueInterpreter;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

public abstract class ContainerValue extends Value {
	private static final String SRC_READ_ERROR_MSG = "Unable to read source";

	public ContainerValue(Type valueType, CharSequence name, CharSequence value ) throws ParseException {
		super( valueType, name, value );
	}

	public ContainerValue(Type valueType, CharSequence name, List<Value> value ){
		super( valueType, name, value );
	}

	public ContainerValue( Type valueType, CharSequence name, Map<String, Value> value ){
		super( valueType, name, value );
	}

	/*
	 * Interprets the char sequence value as a list of Duct Values. Values are accessed from a list by calling it as a function with the index passed as a parameter.
	 * @return A list a Duct values if one exists
	 */
	protected static List<Value> parseValueList( CharSequence baseValue ) throws ParseException {
		List<Value> listValue = new ArrayList<>();
		try {
			StringReader reader = new StringReader( baseValue.toString() );

			Value nextValue;
			do{
				nextValue = ValueInterpreter.parseNextValue( reader );
				if( nextValue != null ){
					listValue.add( nextValue );
				}
			} while( nextValue != null );
		} catch( IOException i ){
			throw new ParseException( ContainerValue.SRC_READ_ERROR_MSG, 0 );
		}

		return listValue;
	}

	/*
	 * Interprets the char sequence value as a set of named duct values. Accessing values from a set is similar to accessing values from a list, only rather than an index, a name is given.
	 * @return A set of named duct values if one exists
	 */
	protected static Map<String, Value> parseValueSet( CharSequence baseValue ) throws ParseException {
		Map<String,Value> set = new HashMap<>();

		//the result of 'interpretList' will always be a super set of the result of 'interpretSet'
		List<Value> valueList = parseValueList( baseValue );

		//if the value has no name then the value can't be accessed so there is no point in adding it to the set.
		for( Value d:valueList ){
			if( d.name != null && !d.name.isEmpty() ){
				set.put( d.name, d );
			}
		}

		return set;
	}
}
