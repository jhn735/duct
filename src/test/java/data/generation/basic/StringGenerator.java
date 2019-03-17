package data.generation.basic;

import data.generation.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;

public class StringGenerator implements DataGenerator<String> {
	@Override
	public String generateValue() {
		return RandomStringUtils.random( 10, true, false );
	}
}
