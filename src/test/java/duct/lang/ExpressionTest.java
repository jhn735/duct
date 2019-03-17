package duct.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.fail;

public class ExpressionTest {

	@DisplayName( "Should parse an empty expression just fine." )
	@Test
	void testBareExpressionParse(){
		try{
			Expression exp = retrieveExpression( "()" );
		} catch( IOException | ParseException e ){
			fail( "Empty expression failed to parse.", e );
		}
	}

	private Expression retrieveExpression( String textToParse ) throws IOException, ParseException {
		Reader r = new StringReader(textToParse);
		return Expression.nextExpression(r);
	}
}
