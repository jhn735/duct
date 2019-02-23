package duct.lang;

import java.io.Reader;
import java.io.IOException;
import java.text.ParseException;

public class ParseUtils {
	private ParseUtils(){}

	public static class PrematureEndOfStreamException extends ParseException {
		private static final String PREMATURE_END_OF_STREAM_MSG = "The passed in value ended prematurely";

		public PrematureEndOfStreamException( int lineNumber ){
			super( PrematureEndOfStreamException.PREMATURE_END_OF_STREAM_MSG, lineNumber );
		}
	}

	public static char readNextChar( Reader reader ) throws IOException, ParseException {
		int curChar = reader.read();

		return convertToChar( curChar );
	}

	public static char readNextChar( Reader reader, Integer characterCount ) throws IOException, ParseException {
		char nextCharacter = ParseUtils.readNextChar( reader );
		characterCount++;

		return nextCharacter;
	}

	public static char convertToChar( int character ) throws ParseException {
		if( character < 0 ){
			throw new PrematureEndOfStreamException( 0 );
		}

		return (char)character;
	}

	public static char readNextCharSkippingWhitespace( Reader reader, Integer characterCount ) throws IOException, ParseException {
		char character;
		do{
			character = readNextChar( reader );
			characterCount++;
		} while( Character.isWhitespace(character) );

		return character;
	}
}
