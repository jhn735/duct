package duct.main.lang;


/**
 * Holds all relevant items within an execution environment.
**/
public class DuctEnv{
  protected Map<String, Object> config; 
  /*Each environment represents something in the way of scope.
   *An expression can run within an environment with access to everything
   * the environment's parent has.
  */
  public final DuctEnv parent;
  public DuctEnv(Map<String, Object> initialConfig){
    config = new HashMap<String, Object>(initialConfig);

  }
  public DuctEnv(DuctEnv env){
    this.parent = env;
  }
  
  public DuctEnv(){
    this.parent = null;
  }

}
