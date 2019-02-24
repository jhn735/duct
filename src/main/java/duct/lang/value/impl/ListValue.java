package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ListValue extends ContainerValue {

	public ListValue( CharSequence name, CharSequence value ) throws ParseException {
		super(Type.LIST, name, value );
	}

	public ListValue( CharSequence name, List<Value> value ){
		super( Type.LIST, name, value );
	}

	@Override
	protected Object convertToBaseValue( CharSequence baseValue ) throws ParseException {
		return ContainerValue.parseValueList( baseValue );
	}

	@Override
	protected Object defaultBaseValue(){
		return new ArrayList<Value>();
	}
}
