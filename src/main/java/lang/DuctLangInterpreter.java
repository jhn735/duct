package duct.main.lang;

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
	public final URL moduleSettingsDirectory;

	public DuctLangInterpreter(URL root){
		this.rootDirectory = root;
		try{
			this.settingsDirectory = new URL(this.rootDirectory, "settings");
			this.scriptDirectory = new URL(this.rootDirectory, "scripts");
			this.moduleDirectory = new URL(this.rootDirectory, "modules");
			this.moduleSettingsDirectory = new URL(this.settingsDirectory, "module");
		} catch (MalformedURLException mal){
			throw new RuntimeException("Error in construction of supporting directory URLs for the interpreter. Fortune does not smile upon you.", mal);
		}

		List<URL> directories = List.of(this.settingsDirectory, this.scriptDirectory, this.moduleDirectory, this.moduleSettingsDirectory);
		createDirectories(directories);
	}

	public DuctLangInterpreter() {
		this( constructDefaultRootDirectory() );
	}

private static final String UNABLE_TO_CREATE_SUPPORTING_DIR_ERR_MSG =
		"Error occurred while creating supporting directories for the interpreter. Something went wrong with the construction of the URLs.";

	/**
	  * Given a list of URLs, creates the directories that they represent if they don't already exist.
	  * @param directories The collection of URLs to create directories for.
	 **/
	private static void createDirectories(Collection<URL> directories) {
		try {
			for( URL url: directories ){
				File dir = new File(url.toURI());
				dir.mkdirs();

				if(!dir.isDirectory())
					throw new RuntimeException("Resource at URL '" + dir.toString() + "' must be a directory and not a file.");
			}
		} catch (URISyntaxException syn){
		 throw new RuntimeException( UNABLE_TO_CREATE_SUPPORTING_DIR_ERR_MSG, syn);	
		}
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
	  * Loads the script with the given path and saves it under the given identifier.
	  * @param identifier The identifier to load the script under.
	  * @param pkg The package under which the script is stored.
	  * @return A Script object which represents the script that was retrieved or null if the script was not found.
	 **/
	public Script loadScript( CharSequence identifier, CharSequence pkg ) {
		Script script = this.scripts.get(identifier.toString());
		
		if(script == null ){
			try {
				URL scriptURL	 = new URL(this.scriptDirectory, pkg.toString()); 
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

}
