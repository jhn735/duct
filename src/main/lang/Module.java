package duct.main.lang;
import java.lang.CharSequence;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.Charset;

/**
 * Making the module an abstract and not an interface so that I can better control
 * how the application is extended. The executor is an interface because there is
 * less of a need control how it does things vs a Module.
 * Also, a module may be instantiated on the fly depending on what direction I 
 * think the project will go.
**/
public abstract class Module{
  private static final Charset DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8;
  //The name of the module which must be set when extending this class.
  public final String Name;

  protected Map<String, byte[]> settings;

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

  /**
    * Retrieves the operations available within this module mapped to their names.
    * @return A Map between the names of the operations and the operation objects available within this module.
   **/
  public Map<String, Operation> operations();
  
  /**
    * Stores the given object into the settings map for long term storage.
    * Unless overriden, it stores the bytes from the results of the object's toString() function with UTF-8 encoding.
    * @param name The name of the property to store the value under.
    * @param obj The object representing the value of the property.
   **/
  public void storeProperty(CharSequence name, Object obj){
    if(settings == null)
      settings = new HashMap<String, byte[]>();
    settings.put(name.toString(), obj.toString().getBytes(DEFAULT_CHARSET));
  }  
  
  /**
    * A function for the retrieval of the modules settings to be store in long term storage. 
    * Storing value in bytes for the module to handle for itself. 
    * Encryption can be done on these values if need be, but the executor may encrypt it anyway.
    * @return A map between the name of the property and the value to be stored in bytes.
   **/ 
  public Map<String, byte[]> settings(){
    if(settings == null)
      settings = new HashMap<String, byte[]>();
  return settings;
  }   
}
