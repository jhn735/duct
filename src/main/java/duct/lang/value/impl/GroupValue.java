package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import duct.lang.value.ValueInterpreter;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

public class GroupValue extends Value {
	private static final String SRC_READ_ERROR_MSG = "Unable to read source";

	private List<Value> valueList;
	private Map<String, Integer> nameToIndexMap;

	public GroupValue( CharSequence name, CharSequence value ) throws ParseException {
		super( Type.GROUP, name);
		this.valueList = parseValueList( value );
		this.nameToIndexMap = createNameToIndexMap( this.valueList );
	}

	public Value getContainedValue( Number valueIndex ){
		return this.valueList.get( valueIndex.intValue() );
	}

	public Value getContainedValue( CharSequence valueName ){
		int index = this.getIndexOfNamedValue( valueName );
		return this.getContainedValue( index );
	}

	private Integer getIndexOfNamedValue( CharSequence valueName ){
		return nameToIndexMap.get( valueName.toString() );
	}

	/*
	 * Interprets the char sequence value as a list of Duct Values. Values are accessed from a list by calling it as a function with the index passed as a parameter.
	 * @return A list a Duct values if one exists
	 */
	private static List<Value> parseValueList( CharSequence baseValue ) throws ParseException {
		List<Value> listValue = new ArrayList<>();
		try {
			StringReader reader = new StringReader( baseValue.toString() );

			Value nextValue;
			do{
				nextValue = ValueInterpreter.parseNextValue( reader );
				if( nextValue != null ){
					listValue.add( nextValue );
				}
			} while( nextValue != null );
		} catch( IOException i ){
			throw new ParseException( GroupValue.SRC_READ_ERROR_MSG, 0 );
		}

		return listValue;
	}

	private static Map<String, Integer> createNameToIndexMap( List<Value> valueList ){
		Map<String, Integer> nameToIndexMap = new HashMap<>();

		for( int index = 0; index < valueList.size(); index++ ){
			Value currentValue = valueList.get( index );
			if( currentValue.hasName() ){
				nameToIndexMap.put( currentValue.getName(), index );
			}
		}

		return nameToIndexMap;
	}

	@Override
	public String getBaseValueAsString() {
		StringBuilder strValue = new StringBuilder();
		for( Value containedValue: this.valueList ){
			strValue.append( containedValue.toString() );
		}
		return strValue.toString();
	}
}
