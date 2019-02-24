package duct.lang.value;

import java.io.Reader;
import java.io.IOException;
import java.lang.CharSequence;
import java.text.ParseException;

import duct.lang.Element;
import duct.lang.Evaluable;
import duct.lang.value.impl.*;

public abstract class Value extends Element implements Evaluable {

	public final Type type;
	protected Object baseValue;

	public Object getBaseValue(){
		return this.baseValue;
	}

	public Value( Type valueType, CharSequence name, CharSequence baseValue ) throws ParseException {
		super( name );
		this.type      = valueType;

		if( baseValue == null || baseValue.toString().isEmpty() ){
			this.baseValue = this.defaultBaseValue();
		} else {
			this.baseValue = this.convertToBaseValue(baseValue);
		}
	}

	public Value( Type valueType, CharSequence name, Object baseValue ){
		super( name );
		this.type      = valueType;
		this.baseValue = baseValue;
	}

	public Value( Type valueType, CharSequence name ){
		super( name );
		this.type      = valueType;
		this.baseValue = this.defaultBaseValue();
	}

	public Value( Value d ){
		super( d.name );
		this.type  = d.type;
		this.baseValue = d.getBaseValue();
	}

	public Value evaluate(){
		return this;
	}

	public String toString(){
		return baseValue.toString();
	}

	protected abstract Object defaultBaseValue();

	protected abstract Object convertToBaseValue( CharSequence baseValue ) throws ParseException;

	public boolean equals( Value obj ){
		return obj.type == this.type && this.baseValue.equals( obj.getBaseValue() );
	}

	public static Value defaultValue( Type valueType, CharSequence name  ){
		try {
			return Value.createValue( valueType, name, "" );
		} catch (ParseException e) {
			return null;
		}
	}

	public static Value defaultValue( Type valueType ){
		return defaultValue( valueType, "" );
	}

	public static Value createValue( Type valueType, CharSequence name, CharSequence value ) throws ParseException {
			switch( valueType ){
				case TEXT:
					return new TextValue( name, value );
				case NUMBER:
					return new NumberValue( name, value );
				case BOOL:
					return new BoolValue( name, value );
				case LIST:
					return new ListValue( name, value );
				case SET:
					return new SetValue( name, value );
			}

		throw new ParseException( ValueInterpreter.UNKNOWN_TYPE_ERR_MSG, 0);
	}

	public static Value nextValue( Reader reader ) throws ParseException, IOException {
		return ValueInterpreter.parseNextValue( reader );
	}
}
