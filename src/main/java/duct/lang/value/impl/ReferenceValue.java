package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;

public class ReferenceValue extends Value {
	private final String ReferencedValueName;

	public ReferenceValue( CharSequence name ) {
		this( name, "" );
	}

	public ReferenceValue( CharSequence name, CharSequence value ){
		super( Type.REFERENCE, name );
		this.ReferencedValueName = value.toString();
	}

	@Override
	public String getBaseValueAsString() {
		return null;
	}
}
