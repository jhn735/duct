package duct.lang.value;

import java.io.Reader;
import java.io.IOException;
import java.lang.CharSequence;
import java.lang.Exception;
import java.text.ParseException;
import duct.lang.Element;
import duct.lang.Evaluable;

public class Value extends Element implements Evaluable {

	public final Type type;
	protected Object value;

	public Object getValue(){
		return this.value;
	}

	protected Value(){
		super( "" );
		this.type  = Type.TEXT;
		this.value = null;
	}

	public Value( Type valueType, CharSequence name, CharSequence value ) throws ParseException {
		super( constructName( valueType, name, value ) );
		this.type = valueType;
		this.value = ValueInterpreter.interpretValue( valueType, value );
	}

	public Value( Value d ){
		super( d.name );
		this.type  = d.type;
		this.value = d.getValue();
	}

	public Value evaluate(){
		return this;
	}

	private static String constructName( Type t, CharSequence name, CharSequence value ){
		String properName = (name == null) ? "" : name.toString();

		if( t == Type.MODULE && properName.isEmpty() ){
			properName = ValueInterpreter.interpretModule( value );
		}

	return properName;
	}

	public String toString(){
		return value.toString();
	}

	public boolean equals( Value obj ) {
		return obj.type == this.type && this.value.equals(obj.getValue());
	}

	public static Value defaultValue( Type valueType ){
		try {
			switch( valueType ){
				case NUMBER: return new Value( valueType, "", "0" );
				case BOOL:   return new Value( valueType, "", "false" );
				default:     return new Value( valueType, "", "" );
			}
		} catch( Exception e ){
			return null;
		}
	}

	public static Value nextValue( Reader reader ) throws ParseException, IOException {
		return ValueInterpreter.parseNextValue( reader );
	}
}
