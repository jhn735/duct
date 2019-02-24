package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import duct.lang.value.ValueInitException;

import java.text.ParseException;

public class NumberValue extends Value {
	public NumberValue( CharSequence name, CharSequence value ) throws ParseException {
		super( Type.NUMBER, name, value );
	}

	public NumberValue( CharSequence name, Number value ){
		super( Type.NUMBER, name, value );
	}

	/**
	 * Interprets the string value as a number. It first tries to interpret as a long and then as a double.
	 **/
	@Override
	protected Object convertToBaseValue( CharSequence value ) throws ParseException {
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

	@Override
	protected Object defaultBaseValue(){
		return 0.0;
	}
}
