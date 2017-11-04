package duct.main.lang;
import java.lang.Long;
import org.apache.commons.lang3.StringUtils;
public class DuctValue {
  public static enum Type {
    TEXT, NUMBER, BOOL, LIST, FUNCTION; 
  }
  
  public final Type type;
  protected Object value;

  public Object getValue(){
    return this.value;
  }

  public DuctValue(){
    this.type = Type.TEXT;
  }

  public DuctValue(Type t, String val){
    this.type = t;
    switch(t){
      case TEXT:     this.value = val; break;
      case NUMBER:   this.value = toNumber(val); break;
      case BOOL:     this.value = toBool(val); break;
      case LIST:     this.value = toList(val); break;
      case FUNCTION: this.value = toFunction(val); break;
    }
    
  }

  public DuctValue(DuctVal d){
    this.type = d.type;
    this.value = d.getValue();
  }

  public String toString(){
    return value.toString();
  }

  
    
  public boolean equals(DuctVal obj){
    return obj.type == this.type && this.value.equals(obj.getValue());
  }
  /**
   * Interprets the string value as a number.
  **/
  public static Number toNumber(String val){
    Long l;
    try{
      l = Long.parseLong(val);
    }catch(NumberFormatException n){
    }
  return l;
  }

  public static Boolean toBool(String val){
    return true;
  }

  public static List<Object> toList(String val){
    return new ArrayList<Object>();
  }

  public static List<String> toFunction(String val){
    return new ArrayList<String>();
  }
}
