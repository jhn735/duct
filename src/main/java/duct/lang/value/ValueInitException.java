package duct.lang.value;

import java.text.ParseException;

public class ValueInitException extends ParseException {
	private static final String BASE_MSG = "Error occurred while initializing value: ";
	public ValueInitException( String s, int errorOffset ){
		super( BASE_MSG + s, errorOffset );
	}
}
