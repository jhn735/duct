package duct.main.lang;
import java.lang.CharSequence;
import java.util.Map;
import java.io.InputStream;

/**
 * An Executor for executing an element of a language and to act as a context type item for that element.
 * Since the mechanisms for storing and retrieving context as well as for executing elements will vary from
 * implementation to implementation, it is best to define this as an interface.
**/
public interface Executor {

	/**
	  * Retrieves the operation which has the given identifier.
	  * @param identifier The identifier of the operation to retrieve.
	  * @return A operation object representing the operation to retrieve.
	 **/
	public Operation getOperation( CharSequence identifier );

	/**
	  * Retrieves the value of the variable which has the given identifier.
	  * @param identifier The identifier of the variable to retrieve.
	  * @return A value object representing the value of the variable retrieved.
	 **/
	public Value getValue( CharSequence identifier );

	/**
	  * Saves the operation with the given identifier.
	  * @param identifier The identifier of the operiation to retrieve.
	  * @param operation The operation to save.
	 **/
	public void saveElement( CharSequence identifier, Operation operation );

	/**
	  * Saves the value of the variable with the given identifier.
	  * @param identifier The identifier of the variable to retrieve.
	  * @param value The value to save. 
	 **/
	public void saveElement( CharSequence identifier, Value value );

	/**
	  * Saves the element with the given identifier.
	  * @param identifier The identifier of the element.
	  * @param element The element to save.
	 **/
	default public void saveElement( CharSequence identifier, Element element ){
		if(element instanceof Value)
			saveElement( identifier, ((Value) element) );
		else if( element instanceof Operation )
			saveElement( identifier, ((Operation) element) );
	}

	/**
	  * Retrieves or loads the module which has the given identifier associated.
	  * That is it retrieves the module which has had the given alias assigned to it.
	  * @param identifier The alias assigned to the requested module.
	  * @return A module object representing the module that was associated with the identifier.
	 **/
	public Module loadModule( CharSequence identifier );

	/**
	  * Loads the script with the given path and saves it under the given identifier.
	  * @param identifier The identifier to load the script under.
	  * @param pkg The package under which the script is stored.
	  * @return A Script object which represents the script that was retrieved.
	 **/
	public Script loadScript( CharSequence identifier, CharSequence pkg );

	/**
	  * Given the name of a module, retrieves a map of stored settings or an empty map if no settings are saved.
	  * @param moduleName The name of the module.
	  * @return A map of settings to be used by the module in question. If no settings are saved an empty map is returned.
	 **/
	public Map<String, byte[]> moduleSettings( CharSequence moduleName );
	
	/**
	  * Given the module and the name of a file, retrieves the contents of a stored file associated with it.
	  * @param module The module associated with the file.
	  * @param fileName The name of the file to be retrieved.
	  * @return An inputStream object representing the file.
	 **/
	public InputStream moduleFile( Module module, CharSequence fileName );

	/**
	  * Executes or evaluates the element with the given name.
	  * @param identifier The name or identifier of the element to execute.
	  * @return The element which is the result of the execution.
	 **/
	public Element execute( CharSequence identifier );
}
