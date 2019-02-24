package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

public class TextValue extends Value {
	public TextValue( CharSequence name, CharSequence value ) throws ParseException {
		super( Type.TEXT, name, value );
	}

	@Override
	protected Object convertToBaseValue( CharSequence baseValue ){
		return StringUtils.remove( baseValue.toString(), '\\');
	}

	@Override
	protected Object defaultBaseValue(){
		return "";
	}
}
