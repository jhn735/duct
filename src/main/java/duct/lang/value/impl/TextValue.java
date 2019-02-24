package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;

public class TextValue extends Value {
	private static final String DEFAULT_VALUE = "";
	private String stringValue;

	public TextValue( CharSequence name, CharSequence value ){
		super( Type.TEXT, name);
		this.stringValue = ( value == null ) ? TextValue.DEFAULT_VALUE : value.toString();
	}

	@Override
	public String getBaseValueAsString() {
		return this.stringValue;
	}
}
