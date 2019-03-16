package duct.lang;
import duct.lang.value.Value;
import java.util.ArrayList;
import java.util.List;

/**
  * Don't care much about implementation, only that when you give it a list of 
  * Values and tell it to execute, it does something and returns a value.
  * You also want to make sure it has a name.
 **/
public abstract class Operation extends Element {

	public Operation( CharSequence name ){
		super( name );
	}

 /**
	 * Executes the operation with the given values as parameters.
	 * @param operands The list of values to be passed in as parameters.
	 * @return A value which is the result of the operation.
	**/
	public Value execute( Executor exe, List<Value> operands ){
		//We don't want the application to crash just because an operation failed.
		operands = (operands == null)
				? new ArrayList<>()
				: Operation.retrieveReferencedValues( exe, operands );

	return doOperation( operands );
	}

	/**
	  * The method which is responsible for actually doing the operation.
	  * @param operands The list of values to be passed in as parameters.
	  * @return A value which is the result of the operation.
	 **/
	abstract protected Value doOperation( List<Value> operands );

	/**
	 * Retrieves referenced values from the executor and places them in the given list.
	 * @param exe The executor from which to retrieve the referenced values.
	 * @param values The list of values which may have to be retrieved.
	 * @return The given list with all references replaced with their referenced.
	 */
	private static List<Value> retrieveReferencedValues( Executor exe, List<Value> values ){
		for( int valueIndex = 0; valueIndex < values.size(); valueIndex++ ){
			Value currentValue = values.get(valueIndex);
			if( currentValue.isReferenceValue() ){
				Value referencedValue = exe.getValue( currentValue.toReferenceValue() );
				values.set( valueIndex, referencedValue );
			}
		}

		return values;
	}
}
