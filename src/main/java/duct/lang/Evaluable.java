package duct.lang;
import java.util.concurrent.Callable;
import duct.lang.value.Value;
/* Each class that is evaluable has the responsibility of defining a way
 * for it to evaluate itself. Context at time of evaluation can only be guaranteed
 * if the implementing class requires it..
 * I have Evaluable extend callable vs having the other classes implement callable 
 * directly because:
 * * It sucks to have to write 'implements Callable<Value>' 
 * * It's easier and important to differentiate evaluables from simple callables.
 * * If the interface needs to be changed, it can be changed.  
 */
public interface Evaluable extends Callable<Value>{
  public Value evaluate();
  default Value call(){
    return evaluate();
  }
}
