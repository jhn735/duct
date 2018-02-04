package duct.main.lang;
import java.util.List;
/**
  * Don't care much about implementation, only that when you give it a list of 
  * Values and tell it to execute, it does something and returns a value.
  * You also want to make sure it has a name.
 **/
public interface Operation {
 /**
   * Retrieves the name of the operation or an empty string if it doesn't have one.
   * @return A string containing the name of the operation or an empty string if the operation has no name.
  **/
 public String getName();

 /**
   * Executes the operation with the given values as parameters.
   * @param values The list of values to be passed in as parameters.
   * @return A value which is the result of the operation.
  **/
 public Value execute(List<Value> values);

}
