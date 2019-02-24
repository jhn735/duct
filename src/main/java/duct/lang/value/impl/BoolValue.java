package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import duct.lang.value.ValueInitException;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class BoolValue extends Value {
	private static List<String> boolStringValues = Arrays.asList( "true", "false" );

	public BoolValue( CharSequence name, CharSequence value ) throws ParseException {
		super( Type.BOOL, name, value );
	}

	public BoolValue( CharSequence name, Boolean value ){
		super( Type.BOOL, name, value );
	}

	/**
	 * Interprets the string value as a boolean.
	 * @return If the value is either 'true' or 'false' it returns that value else,
	 *		an exception is thrown.
	 **/
	protected Object convertToBaseValue( CharSequence value ) throws ParseException {
		value = (value == null)? "" : value.toString().toLowerCase();

		if( !boolStringValues.contains( value ) ){
			throw new ValueInitException( "Value given '" + value + "' is not a boolean value.", 0 );
		}

		return Boolean.parseBoolean( value.toString() );
	}

	protected Object defaultBaseValue(){
		return false;
	}
}
