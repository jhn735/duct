package duct.main.lang.interpreter;

import duct.main.lang.*;
import duct.main.lang.Module;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import java.lang.CharSequence;
import java.lang.RuntimeException;
import java.lang.System;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
  * Interpreter for the Duct language.
  * Anything which goes wrong in the constructor, will cause a runtime exception to occur and likely kill the program. 
  * I *want* this to happen. If something goes wrong here the rest of the program will not function properly and so needs to be fixed.
 **/
public class DuctLangInterpreter implements Executor {
	private Map<String, Operation> operations;
	private Map<String, Evaluable> evaluables;
	private Map<String, Module> modules;
	private Map<String, Script> scripts;

	private URLClassLoader moduleLoader;

	private static final String HOME_PROPERTY = "user.home";

	public final URL rootDirectory;
	public final URL settingsDirectory;
	public final URL scriptDirectory;
	public final URL moduleDirectory;
	public final InterpreterAgent outputAgent;
	public final URL moduleSettingsDirectory;

	public DuctLangInterpreter(URL root){
		this.rootDirectory = root;
		try{
			this.settingsDirectory       = new URL(this.rootDirectory,     "settings");
			this.scriptDirectory         = new URL(this.rootDirectory,     "scripts" );
			this.moduleDirectory         = new URL(this.rootDirectory,     "modules" );
			this.outputAgent             = new ProgramOutput( new URL(this.rootDirectory,     "logs"    ) );
			this.moduleSettingsDirectory = new URL(this.settingsDirectory, "module"  );

		} catch (MalformedURLException mal){
			throw new RuntimeException("Error in construction of supporting directory URLs for the interpreter. Fortune does not smile upon you.", mal);
		}
	}

	public DuctLangInterpreter() {
		this( constructDefaultRootDirectory() );
	}

	public Value interpretStatement( CharSequence statementText) {
		return null;
	}

	private static final String ROOT_DIR_CONST_ERR_MSG =
		"Error in constructing the default root directory URL of the interpreter. Check that the 'user.home' property in java is properly set.";
	private static URL constructDefaultRootDirectory() {
		URL rootURL;

		String home = System.getProperty(HOME_PROPERTY);
		if(home == null)
			throw new RuntimeException("System property '" + HOME_PROPERTY + "' does not exist for some reason!"); 

		try {
			rootURL = new URL(home + "/.duct");	
		} catch(MalformedURLException mal){
			throw new RuntimeException(ROOT_DIR_CONST_ERR_MSG, mal);
		}

	return rootURL;
	}

	/**
	  * Retrieves the operation which has the given identifier.
	  * @param identifier The identifier of the operiation to retrieve.
	  * @return A operation object representing the operation to retrieve.
	 **/
	public Operation getOperation( CharSequence identifier ){
		return operations.get(identifier.toString());
	}

	/**
	  * Retrieves the value of the variable which has the given identifier.
	  * @param identifier The identifier of the variable to retrieve.
	  * @return A value object representing the value of the variable retrieved.
	 **/
	public Value getValue( CharSequence identifier ){
		return evaluables.get(identifier.toString()).evaluate();
	}

	/**
	  * Saves the operation with the given identifier.
	  * @param identifier The identifier of the operiation to retrieve.
	  * @param operation The operation to save.
	 **/
	public void saveElement( CharSequence identifier, Operation operation ){
		operations.put(identifier.toString(), operation);
	}

	/**
	  * Saves the value of the variable with the given identifier.
	  * @param identifier The identifier of the variable to retrieve.
	  * @param value The value to save. 
	 **/
	public void saveElement( CharSequence identifier, Value value ){
		evaluables.put(identifier.toString(), value);
	}

	/**
	  * Retrieves or loads the module which has the given identifier associated. 
	  * That is it retrieves the module which has had the given alias assigned to it. 
	  * @param identifier The alias assigned to the requested module.
	  * @return A module object representing the module that was associated with the identifier.
	 **/
	public Module loadModule( CharSequence identifier ){
		//if the module has already been loaded, then return it and do nothing
		Module module = modules.get(identifier.toString());
		if(module != null)
			return module;

	return module;
	}

	/**
	  * Loads and executes the given script and package.
	  * @param identifier The identifier to load the script under.
	  * @param pkg The package under which the script is stored.
	 **/
	public void runScript( CharSequence identifier, CharSequence pkg ) {
		Script scriptToRun = loadScript( identifier, pkg );
		scriptToRun.run();
	}

	/**
	  * Loads the script with the given path and saves it under the given identifier.
	  * @param identifier The identifier to load the script under.
	  * @param pkg The package under which the script is stored.
	  * @return A Script object which represents the script that was retrieved or null if the script was not found.
	 **/
	public Script loadScript( CharSequence identifier, CharSequence pkg ) {
		Script script = this.scripts.get(identifier.toString());
		
		if(script == null ){
			try {
				URL scriptURL   = new URL(this.scriptDirectory, pkg.toString());
				File scriptFile = new File(scriptURL.toURI());
				FileReader scriptReader = new FileReader(scriptFile);
				script = this.scripts.put(identifier.toString(), Script.nextScript(scriptReader, this));
			} catch (Exception e){
			}
		}

		return script;
	}

	/**
	  * Given the name of a module, retrieves a map of stored settings or an empty map if no settings are saved.
	  * @param moduleName The name of the module.
	  * @return A map of settings to be used by the module in question. If no settings are saved an empty map is returned.
	 **/
	public Map<String, byte[]> moduleSettings( CharSequence moduleName ){
		return null;
	}

	/**
	  * Given the module and the name of a file, retrieves the contents of a stored file associated with it.
	  * @param module The module associated with the file. 
	  * @param fileName The name of the file to be retrieved.
	  * @return An inputStream object representing the file.
	 **/
	public InputStream moduleFile( Module module, CharSequence fileName ){
		return null;
	}

	/**
	  * Executes or evaluates the element with the given name.
	  * @param identifier The name or identifier of the element to execute.
	  * @return The element which is the result of the execution.
	 **/
	public Element execute( CharSequence identifier ){
		return null;
	}

	/**
	  * Displays the string value of the Duct Value to wherever the text output of this interpreter has been directed to..
	  * @param val The value to display.
	 **/
	public void displayValue( Value val ) {

	}
}
