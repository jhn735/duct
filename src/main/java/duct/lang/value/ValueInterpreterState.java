package duct.lang.value;

import duct.lang.ParseUtils;
import duct.lang.value.type.Type;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

class ValueInterpreterState {
	private Reader reader;

	private StringBuilder extractedValue;

	private Integer   characterCount;
	private Character currentCharacter;
	private Integer   startOfValue;
	private Long      enclosureDepth;
	private Type      typeOfValue;
	private String    nameOfValue;

	ValueInterpreterState( Reader r ){
		this.reader = r;

		this.extractedValue   = new StringBuilder();
		this.characterCount   = 0;
		this.currentCharacter = null;
		this.startOfValue     = -1;
		this.enclosureDepth   =  0L;
		this.typeOfValue      = null;
		this.nameOfValue      = null;
	}

	private void resetValues(){
		this.resetExtractedValue();
		this.characterCount   = 0;
		this.currentCharacter = ' ';
		this.startOfValue     = -1;
		this.enclosureDepth   = 0L;
		this.typeOfValue      = null;
		this.nameOfValue      = null;
	}

	long getEnclosureDepth(){
		return this.enclosureDepth;
	}

	void increaseEnclosureDepth(){
		this.enclosureDepth++;
	}

	void decreaseEnclosureDepth(){
		if( this.enclosureDepth > 0 ){
			this.enclosureDepth--;
		}
	}

	boolean isEnclosureDepthNonZero(){
		return this.enclosureDepth > 0;
	}

	int getCharacterCount(){
		return this.characterCount;
	}

	int getStartOfValue(){
		return this.startOfValue;
	}

	void setStartOfValue( int nStartOfValue ){
		this.startOfValue = nStartOfValue;
	}

	char getCurrentCharacter(){
		return this.currentCharacter;
	}

	char readNextCharacterFromReader() throws IOException, ParseException {
		this.currentCharacter =
				ParseUtils.readNextChar( this.reader, this.characterCount );
		return this.currentCharacter;
	}

	char readNextCharacterSkippingWhitespaceFromReader() throws IOException, ParseException {
		this.currentCharacter =
				ParseUtils.readNextCharSkippingWhitespace( this.reader, this.characterCount );
		return this.currentCharacter;
	}

	Type getTypeOfValue(){
		return this.typeOfValue;
	}

	void addCurrentCharacterToExtractedValue(){
		this.extractedValue.append( this.currentCharacter );
	}

	void resetExtractedValue(){
		if( this.extractedValue == null ){
			this.extractedValue = new StringBuilder();
		} else {
			this.extractedValue.setLength( 0 );
		}
	}

	Type extractTypeFromExtractedValue() throws ParseException {
		this.typeOfValue = Type.parseType( this.extractedValue );
		this.resetExtractedValue();
		return this.typeOfValue;
	}

	String extractNameFromExtractedValue(){
		this.nameOfValue = this.extractedValue.toString();
		this.resetExtractedValue();
		return this.nameOfValue;
	}

	Value toValue() throws ParseException {
		return new Value( this.typeOfValue, this.nameOfValue, this.extractedValue );
	}
}
