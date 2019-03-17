package data.generation.value;

import data.generation.DataGenerator;
import duct.lang.value.Type;
import duct.lang.value.Value;

import java.util.*;

public class DuctValueGenerator implements DataGenerator<Value> {
	private List<DuctValueGenerator> _generators;
	private int _currentGeneratorIndex;

	public DuctValueGenerator(){
		this._generators = DuctValueGenerator.createValueGenerators();
		this._currentGeneratorIndex = 0;
	}

	@Override
	public Value generateValue(){
		return this.getCurrentGenerator().generateValue();
	}

	private DuctValueGenerator getCurrentGenerator(){
		return this._generators.get( this.getGeneratorIndex() );
	}

	private int getGeneratorIndex(){
		this._currentGeneratorIndex++;

		if( this._currentGeneratorIndex >= this._generators.size() ){
			this._currentGeneratorIndex = 0;
		}

		return this._currentGeneratorIndex;
	}

	private static List<DuctValueGenerator> createValueGenerators(){
		List<DuctValueGenerator> generatorSet = new ArrayList<>();

		for( Type currentType:Type.values() ){
			generatorSet.add( createValueGenerator( currentType ) );
		}

		return Collections.unmodifiableList( generatorSet );
	}

	public static DuctValueGenerator createValueGenerator( Type generatorType ){
		switch( generatorType ){
			case TEXT:
			case NUMBER:
			case BOOL:
			case GROUP:
			case REFERENCE:
		}
		return null;
	}
}
