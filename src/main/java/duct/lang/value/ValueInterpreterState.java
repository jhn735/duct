package duct.lang.value;

import duct.lang.ParseUtils;

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

	public ValueInterpreterState( Reader r ){
		this.reader = r;

		this.extractedValue   = new StringBuilder();
		this.characterCount   = 0;
		this.currentCharacter = null;
		this.startOfValue     = -1;
		this.enclosureDepth   = new Long( 0 );
		this.typeOfValue      = null;
		this.nameOfValue      = null;
	}

	private void resetValues(){
		this.resetExtractedValue();
		this.characterCount   = 0;
		this.currentCharacter = ' ';
		this.startOfValue     = -1;
		this.enclosureDepth   = new Long( 0 );
		this.typeOfValue      = null;
		this.nameOfValue      = null;
	}

	public long getEnclosureDepth(){
		return this.enclosureDepth;
	}

	public void increaseEnclosureDepth(){
		this.enclosureDepth++;
	}

	public void decreaseEnclosureDepth(){
		if( this.enclosureDepth > 0 ){
			this.enclosureDepth--;
		}
	}

	public boolean isEnclosureDepthNonZero(){
		return this.enclosureDepth > 0;
	}

	public int getCharacterCount(){
		return this.characterCount;
	}

	public void increaseCharacterCount(){
		this.characterCount++;
	}

	public int getStartOfValue(){
		return this.startOfValue;
	}

	public void setStartOfValue( int nStartOfValue ){
		this.startOfValue = nStartOfValue;
	}

	public char getCurrentCharacter(){
		return this.currentCharacter;
	}

	public char readNextCharacterFromReader() throws IOException, ParseException {
		this.currentCharacter =
				ParseUtils.readNextChar( this.reader, this.characterCount );
		return this.currentCharacter;
	}

	public char readNextCharacterSkippingWhitespaceFromReader() throws IOException, ParseException {
		this.currentCharacter =
				ParseUtils.readNextCharSkippingWhitespace( this.reader, this.characterCount );
		return this.currentCharacter;
	}

	public Type getTypeOfValue(){
		return this.typeOfValue;
	}

	public void addCurrentCharacterToExtractedValue(){
		this.extractedValue.append( this.currentCharacter );
	}

	public void resetExtractedValue(){
		if( this.extractedValue == null ){
			this.extractedValue = new StringBuilder();
		} else {
			this.extractedValue.setLength( 0 );
		}
	}

	public Type extractTypeFromExtractedValue() throws ParseException {
		this.typeOfValue = Type.parseType( this.extractedValue );
		this.resetExtractedValue();
		return this.typeOfValue;
	}

	public String getNameOfValue(){
		return this.nameOfValue;
	}

	public String extractNameFromExtractedValue(){
		this.nameOfValue = this.extractedValue.toString();
		this.resetExtractedValue();
		return this.nameOfValue;
	}

	public Value toValue() throws ParseException {
		return new Value( this.typeOfValue, this.nameOfValue, this.extractedValue );
	}
}
