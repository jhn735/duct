package duct.main.lang;
import java.lang.CharSequence;
import java.util.Map;
import java.util.HashMap;

/**
 * Making the module an abstract and not an interface so that I can better control
 * how the application is extended. The executor is an interface because there is
 * less of a need control how it does things vs a Module.
 * Also, a module may be instantiated on the fly depending on what direction I 
 * think the project will go.
**/
public abstract class Module{
  //The name of the module which must be set when extending this class.
  public final String Name;

  /**
    * Please note that the name must not be null otherwise this will be problem for your module.
   **/
  public Module(CharSequence name, Map<String, byte[]> settings){
    this.Name = name.toString();
    this.settings = new HashMap<String, byte[]>(settings); 
  } 
  
  public Module(CharSequence name){
   this.Name = name.toString(); 
   this.settings = new HashMap<String, byte[]>() 
  }

  public List<String> operationNames();
  public Operation getOperation(CharSequence name);
  public Map<String, Operation> operations();
  
  /**
    * A function for the retrieval of the modules settings to be store in long term storage. 
    * Storing value in bytes for the module to handle for itself. 
    * Encryption can be done on these values if need be, but the executor may encrypt it anyway.
    * @return A map between the name of the property and the value to be stored in bytes.
   **/ 
  public Map<String, byte[]> settings();   
}
