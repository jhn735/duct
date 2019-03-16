package duct.lang.value;

import java.io.Reader;
import java.io.IOException;
import java.lang.CharSequence;
import java.text.ParseException;

import duct.lang.Element;
import duct.lang.Evaluable;
import duct.lang.Executor;
import duct.lang.value.impl.*;

public abstract class Value extends Element implements Evaluable {
	public final Type type;

	public Value( Type valueType, CharSequence name ){
		super( name );
		this.type      = valueType;
	}

	public Value evaluate(){
		return this;
	}

	public Value evaluate( Executor exe ){
		return this;
	}
	public abstract String getBaseValueAsString();

	@Override
	public String toString(){
		StringBuilder strValue = new StringBuilder();
		strValue.append('<');
		if( this.hasName() ) {
			strValue.append(this.name).append('#');
		}
		strValue.append( this.type.name().toUpperCase()).append(':');
		strValue.append( this.getBaseValueAsString() );
		strValue.append('>');

		return strValue.toString();
	}

	public GroupValue toGroupValue(){
		return new GroupValue( this.getName(), this );
	}

	public ReferenceValue toReferenceValue(){
		return new ReferenceValue( "REF_" + this.getName(), this.getName() );
	}

	public boolean isReferenceValue(){
		return this.type == Type.REFERENCE;
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
					return new TextValue(      name, value );
				case NUMBER:
					return new NumberValue(    name, value );
				case BOOL:
					return new BoolValue(      name, value );
				case GROUP:
					return new GroupValue(     name, value );
				case REFERENCE:
					return new ReferenceValue( name, value );
			}

		throw new ParseException( ValueInterpreter.UNKNOWN_TYPE_ERR_MSG, 0);
	}

	public static Value nextValue( Reader reader ) throws ParseException, IOException {
		return ValueInterpreter.parseNextValue( reader );
	}
}
