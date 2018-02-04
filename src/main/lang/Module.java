package duct.main.lang;
import java.lang.CharSequence;
import java.util.Map;

/**
 * Making the module an abstract and not an interface so that I can better control
 * how the application is extended. The executor is an interface because there is
 * less of a need control how it does things vs a Module.
 * Also, a module may be instantiated on the fly depending on what direction I 
 * think the project will go.
**/
public abstract class Module{
  
  public List<String> operationNames();
  public Operation getOperation(CharSequence name);
  public Map<String, Operation> operations();
}
