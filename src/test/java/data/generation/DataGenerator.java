package data.generation;

import java.util.ArrayList;
import java.util.Collection;

public interface DataGenerator<K> {
	K generateValue();

	default Collection<K> generateValues( int collectionSize ){
		Collection<K> generatedValues = new ArrayList<>();

		for(;collectionSize > 0; collectionSize-- ){
			generatedValues.add( generateValue() );
		}

		return generatedValues;
	}
}
