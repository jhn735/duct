package duct.main.lang;

import java.io.Reader;
import java.lang.Runnable;
import java.util.List;
import java.util.Set;

/**
  * An object to represent a script, interpreted and packaged to be run whenever.
 **/
public class Script extends Element implements Runnable{
  public final Executor executor;
  private final Set<Module> modules;
  private final List<Evaluable> instructions;

  public Script(CharSequence name, List<Evaluable> instructions, Executor exe, Set<Module> modules){ 
    super(name);
    this.executor = exe;
    this.modules = modules;
    this.instructions = instructions;    
  }

  public void run(){
    for(Evaluable e:instructions)
      e.evaluate();
  }
  
  public static Script nextScript(Reader reader, Executor exe){
  return null;
  }  
}
