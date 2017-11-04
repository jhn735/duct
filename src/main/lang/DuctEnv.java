package duct.main.lang;
import duct.main.lang.DuctVal;
import java.util.Map;
import java.util.HashMap;

/**
 * Holds all relevant items within an execution environment.
**/
public class DuctEnv extends HashMap<String, DuctVal>{
  /*Each environment represents something in the way of scope.
   *An expression can run within an environment with access to everything
   * the environment's parent has.
  */
  public final DuctEnv parent;
  public DuctEnv(Map<String, Object> initialConfig){
    //config = new HashMap<String, Object>(initialConfig);
    parent = null;

  }
  public DuctEnv(DuctEnv env){
    this.parent = env;
  }
  
  public DuctEnv(){
    this.parent = null;
  }

}
