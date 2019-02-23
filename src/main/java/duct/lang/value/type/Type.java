package duct.lang.value.type;

import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;

public enum Type {

	TEXT(false), NUMBER(false), BOOL(false), MODULE(false), LIST(true), SET(true);

	public final boolean isContainer;

	Type( boolean isCont ){
		this.isContainer = isCont;
	}

	public static Type parseType( CharSequence extractedTypeName ) throws ParseException {
		for(Type t:Type.values()){
			if( matchesType( t, extractedTypeName ) ) {
				return t;
			}
		}

	 //if no type was found despite the presence of the ':' then error out. 
	 throw new ParseException( "Type specifier '" + extractedTypeName + "' is not valid.", 0 );
	}

	private static boolean matchesType( Type t, CharSequence extractedTypeName ){
		String name = t.name();
		//instead of specifying the whole name of the type, one can specify the first letter of the type name 
		return StringUtils.equalsIgnoreCase( name, extractedTypeName ) ||
			( extractedTypeName.length() == 1 && StringUtils.startsWithIgnoreCase(name, extractedTypeName) );
	}
}
