package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class SetValue extends ContainerValue {
	public SetValue(CharSequence name, CharSequence value ) throws ParseException {
		super(Type.SET, name, value);
	}

	public SetValue( CharSequence name, Map<String, Value> value ){
		super( Type.SET, name, value );
	}

	@Override
	protected Object convertToBaseValue( CharSequence baseValue ) throws ParseException {
		return ContainerValue.parseValueSet( baseValue );
	}

	@Override
	protected Object defaultBaseValue(){
		return new HashMap<String, Value>();
	}
}
