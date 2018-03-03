package duct.main.lang;

import java.util.Set;
import java.util.List;
import java.lang.Runnable;

/**
  * An object to represent a script, interpreted and packaged to be run whenever.
 **/
public class Script extends Element implements Runnable{
  public final Executor executor;
  private final Set<Module> modules;
  private final List<Evaluable> instructions;

  public Script(CharSequence name, List<Evaluable> instructions){ 
    super(name);
    this.executor = exe;
    this.modules = modules;
    this.instructions = instructions;    
  }

  public void run(){
    for(Evaluable e:instructions)
      e.Evaluate();
  }
  
}
