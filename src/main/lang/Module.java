package duct.main.lang;
import java.lang.CharSequence;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.Charset;

/**
 * Making the module an abstract and not an interface so that I can better control
 * how the application is extended. The executor is an interface because there is
 * less of a need control how it does things vs a Module.
 * A module can be considered an immutable set of operations.
**/
public abstract class Module extends HashSet<Operation>{
  private static final Charset DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8;
  //The name of the module which must be set when extending this class.
  public final String name;

  public final Executor executor;
  protected Map<String, byte[]> settings;

  /**
    * Please note that the name must not be null otherwise this will be problem for your module.
   **/
  public Module(CharSequence name, Collection<Operation> operations, Executor exe, Map<String, byte[]> settings){
    super(operations);
    this.name = name.toString();
    this.settings = new HashMap<String, byte[]>(settings); 
    this.executor = exe;
  } 
  
  public Module(CharSequence name, Collection<Operation> operations, Executor exe){
   super(operations);
   this.name = name.toString(); 
   this.settings = new HashMap<String, byte[]>(); 
   this.executor = exe;
  }

  /**
    * Overrided the 'add' method to prevent changing the contents of the set.
   **/
  @Override
  public boolean add(Operation e){
    return this.contains(e);
  }

  
  /**
    * Overrided the 'remove' method to prevent changing the contents of the set.
   **/
  @Override
  public boolean remove(Object o){
    return this.contains(o);
  }

  /**
    * Stores the given object into the settings map for long term storage.
    * Unless overriden, it stores the bytes from the results of the object's toString() function with UTF-8 encoding.
    * @param name The name of the property to store the value under.
    * @param obj The object representing the value of the property.
   **/
  public void storeProperty(CharSequence name, Object obj){
    this.storeProperty(name, obj.toString());
  }  

  /**
    * Stores the given object into the settings map for long term storage.
    * Unless overriden, it stores the bytes from the given string's UTF-8 encoding.
    * @param name The name of the property to store the value under.
    * @param obj The string representing the value of the property.
   **/
  public void storeProperty(CharSequence name, String obj){
    this.settings().put(name.toString(), obj.getBytes(DEFAULT_CHARSET));
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
