package duct.main.lang;
import java.lang.CharSequence;

/**
 * Don't care how the executor does what it needs to do. Different 
 * implementations are more likely to need to be different.
**/
public interface Executor {
  public Operation getOperation(CharSequence identifier);
  public Value getValue(CharSequence identifier);    
  public Map<String, String> moduleSettings(CharSequence moduleName);
}
