package duct.main.lang;
import java.util.List;
/**
  * Don't care much about implementation, only that when you give it a list of 
  * Values and tell it to execute, it does something and returns a value.
  * You also want to make sure it has a name.
 **/
public interface Operation {
 public String getName();
 public Value execute(List<Value> values);

}
