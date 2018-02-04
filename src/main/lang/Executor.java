package duct.main.lang;
import java.lang.CharSequence;

/**
 * Don't care how the executor does what it needs to do. Different 
 * implementations are more likely to need to be different.
**/
public interface Executor {
  /** 
    * Retrieves the operation which has the given identifier.
    * @param identifier The identifier of the operiation to retrieve.
    * @return A operation object representing the operation to retrieve.
   **/
  public Operation getOperation(CharSequence identifier);

  /**
    * Retrieves the value of the variable which has the given identifier.
    * @param identifier The identifier of the variable to retrieve.
    * @return A value object representing the value of the variable retrieved.
   **/
  public Value getValue(CharSequence identifier);    
  
  /** 
    * Given the name of a module, retrieves a map of stored settings or an empty map if no settings are saved.
    * @param moduleName The name of the module.
    * @return A map of settings to be used by the module in question. If no settings are saved an empty map is returned.
   **/
  public Map<String, String> moduleSettings(CharSequence moduleName);
}
