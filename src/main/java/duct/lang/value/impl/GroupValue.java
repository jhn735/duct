package duct.lang.value.impl;

import duct.lang.value.Type;
import duct.lang.value.Value;
import duct.lang.value.ValueInterpreter;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

public class GroupValue extends Value implements Collection<Value>{
	private static final String SRC_READ_ERROR_MSG = "Unable to read source";

	private List<Value>          valueList;
	private Map<String, Integer> nameToIndexMap;

	protected GroupValue( CharSequence name ){
		super( Type.GROUP, name );
		this.valueList      = new ArrayList<>();
		this.nameToIndexMap = new HashMap<>();
	}

	public GroupValue( CharSequence name, CharSequence value ) throws ParseException {
		this( name );
		this.parseValuesFromBaseValue( value );
	}

	public GroupValue( CharSequence name, Value value ){
		this( name );
		this.addNewValue( value );
	}

	public Value get( Number valueIndex ){
		return this.valueList.get( valueIndex.intValue() );
	}

	public Value get( CharSequence valueName ){
		int index = this.getIndexOfNamedValue( valueName );
		return this.get( index );
	}

	private Integer getIndexOfNamedValue( CharSequence valueName ){
		return nameToIndexMap.get( valueName.toString() );
	}

	private void addNewValue( Value newValue ){
		Integer nextIndex = this.valueList.size();
		this.valueList.add( newValue );

		if( !newValue.getName().isEmpty() ){
			this.nameToIndexMap.put( newValue.getName(), nextIndex );
		}
	}

	private void parseValuesFromBaseValue( CharSequence baseValue ) throws ParseException {
		try {
			StringReader reader = new StringReader( baseValue.toString() );

			Value nextValue;
			do{
				nextValue = ValueInterpreter.parseNextValue( reader );
				if( nextValue != null ){
					this.addNewValue( nextValue );
				}
			} while( nextValue != null );
		} catch( IOException i ){
			throw new ParseException( GroupValue.SRC_READ_ERROR_MSG, 0 );
		}
	}

	@Override
	public String getBaseValueAsString() {
		StringBuilder strValue = new StringBuilder();
		for( Value containedValue: this.valueList ){
			strValue.append( containedValue.toString() );
		}
		return strValue.toString();
	}

	@Override
	public GroupValue toGroupValue(){
		return this;
	}

	@Override
	public int size() {
		return this.valueList.size();
	}

	@Override
	public boolean isEmpty() {
		return this.valueList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.valueList.contains(o);
	}

	@Override
	public Iterator<Value> iterator() {
		return this.valueList.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.valueList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.valueList.toArray(a);
	}

	@Override
	public boolean add(Value value) {
		Integer indexOfExistingValue = this.nameToIndexMap.get( value.getName() );
		if( indexOfExistingValue != null ){
			this.valueList.set( indexOfExistingValue, value );
		} else {
			this.addNewValue( value );
		}

		return true;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException( "Group Value does not support removing values." );
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.valueList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Value> c) {
		for( Value value: c ){
			this.add( value );
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException( "Group Value does not support removing values." );
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException( "Group Value does not support removing values" );
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException( "Group Value does not support removing values" );
	}
}
