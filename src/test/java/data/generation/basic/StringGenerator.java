package data.generation.basic;

import data.generation.DataGenerator;
import data.generation.basic.number.IntegerGenerator;
import org.apache.commons.lang3.RandomStringUtils;

public class StringGenerator implements DataGenerator<String> {
	private final IntegerGenerator _stringSizeGenerator;

	private final boolean _letters;
	private final boolean _numbers;

	public StringGenerator( int maxStringSize, boolean letters, boolean numbers ){
		this._stringSizeGenerator = new IntegerGenerator( maxStringSize, 0 );
		this._letters = letters;
		this._numbers = numbers;
	}

	public StringGenerator( int maxStringSize ){
		this( maxStringSize, true, false );
	}

	@Override
	public String generateValue() {
		return RandomStringUtils.random( this.getStringSize(), this._letters, this._numbers );
	}

	private Integer getStringSize(){
		return _stringSizeGenerator.generateValue();
	}
}
