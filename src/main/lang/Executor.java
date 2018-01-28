package duct.main.lang;
import java.lang.CharSequence;

public interface Executor {
  public Operation getOperation(CharSequence identifier);
  public Value getValue(CharSequence identifier);    
}
