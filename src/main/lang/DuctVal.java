package duct.main.lang;

public class DuctVal {
  public static enum Type {
    TEXT, NUMBER, BOOL, LIST, FUNCTION; 
  }
  
  public final Type type;
  protected Object value;

  public Object getValue(){
    return this.value;
  }

  public DuctVal(){
    this.type = Type.TEXT;
  }

  public DuctValue(Type t, String val){
    switch(t){
      case TEXT:     this.value = val; break;
      case NUMBER:   this.value = toNumber(val); break;
      case BOOL:     this.value = toBool(val); break;
      case LIST:     this.value = toList(val); break;
      case FUNCTION: this.value = toFunction(val); break;
    }
    
  }

  public DuctVal(DuctVal d){
    this.type = d.type;
    this.value = d.getValue();
  }

  public String toString(){
    return value.toString();
  }

  
    
  public boolean equals(DuctVal obj){
    return obj.type == this.type && this.value.equals(obj.getValue());
  }

  public static Number toNumber(String val){
    return 4.56;
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
