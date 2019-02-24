package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import duct.lang.value.ValueInitException;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class BoolValue extends Value {
	private static final List<String> BOOL_STRING_VALUES = Arrays.asList( "true", "false" );

	private Boolean booleanValue;

	public BoolValue( CharSequence name, CharSequence value ) throws ParseException {
		this( name, BoolValue.convertToBool( value ) );
	}

	public BoolValue( CharSequence name, Boolean value ){
		super( Type.BOOL, name );
		this.booleanValue = value;
	}

	/**
	 * Interprets the string value as a boolean.
	 * @return If the value is either 'true' or 'false' it returns that value else,
	 *		an exception is thrown.
	 **/
	private static Boolean convertToBool( CharSequence value ) throws ParseException {
		value = (value == null)? "" : value.toString().toLowerCase();

		if( !BoolValue.BOOL_STRING_VALUES.contains( value ) ){
			throw new ValueInitException( "Value given '" + value + "' is not a boolean value.", 0 );
		}

		return Boolean.parseBoolean( value.toString() );
	}

	@Override
	public String getBaseValueAsString() {
		return this.booleanValue.toString();
	}
}
