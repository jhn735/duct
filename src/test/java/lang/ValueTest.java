package duct.test.lang;

import java.text.ParseException;
import duct.main.lang.Value;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.CharSequence;
import java.lang.System;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValueTest {


	@DisplayName("Should calculate the correct sum")
	@ParameterizedTest(name = "{index} => parseText={0}, expectedType={1}, expectedValue={2}, negativeTest={3}")
	@CsvSource({
		"'<Text:hello world!!>', 'TEXT', 'hello world!!', false",
		"'<Number:35>', 'NUMBER', '32', false"
	})
	void testValueParse( String parseText, String expectedType, String expectedValue, boolean negativeTest ) {
		// do a positive test.
		Value val;
		try {	
			val = retrieveValue(parseText);
			String type = val.type.name();
			if(!expectedType.equalsIgnoreCase(type))
				fail("Type of value '" + type + "' does not match expected type, '" + expectedType + "'");

			if(!expectedValue.equalsIgnoreCase(val.toString()))
				fail("Value '" + val.toString() + "' does not match the expected value '" + expectedValue + "'");
		} catch(Exception e ) {
			if(!negativeTest)
				fail(e.getMessage());
		}
	}



	private Value retrieveValue( CharSequence textToParse ) throws ParseException, IOException {
		Reader r = new StringReader( textToParse.toString() );
	return Value.nextValue(r);
	}
}
