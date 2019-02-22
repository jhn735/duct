package duct.lang;

import java.io.Reader;
import java.io.IOException;
import java.text.ParseException;

public class ParseUtils {
	private ParseUtils(){}

	private static final String PREMATURE_END_OF_STREAM_MSG = "The passed in value ended prematurely";

	public static char readNextChar( Reader reader ) throws IOException, ParseException {
		int curChar = reader.read();

		if( curChar < 0 ) {
			throw new ParseException(ParseUtils.PREMATURE_END_OF_STREAM_MSG, 0);
		}

		return (char)curChar;
	}

}
