package duct.main.lang;
import java.util.List;
/**
  * Don't care much about implementation, only that when you give it a list of 
  * DuctValues and tell it to execute, it does something and returns a ductValue.
  * You also want to make it has a name.
 **/
public interface DuctOperation {
 public String getName();
 public DuctValue execute(List<DuctValue> values);

}
