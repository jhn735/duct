package duct.main.lang;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
/**
  * Don't care much about implementation, only that when you give it a list of 
  * Values and tell it to execute, it does something and returns a value.
  * You also want to make sure it has a name.
 **/
public abstract class Operation extends Element{ 
	public final Module module;

	public Operation(CharSequence name, Module module){
		super(name);
		this.module = module;
	}

 /**
	 * Executes the operation with the given values as parameters.
	 * @param operands The list of values to be passed in as parameters.
	 * @return A value which is the result of the operation.
	**/
	public Value execute(List<Value> operands){
		//We don't want the application to crash just because an operation failed.
		if( operands == null )
			operands = new ArrayList<Value>();

	return doOperation( operands );
	}

	/**
	  * The method which is responsible for actually doing the operation.
	  * @param operands The list of values to be passed in as parameters.
	  * @return A value which is the result of the operation.
	 **/
	abstract protected Value doOperation( List<Value> operands );
}
