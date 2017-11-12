package duct.main.lang;
import java.lang.Long;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Long;
import java.lang.Double;
import org.apache.commons.lang3.StringUtils;
import java.lang.Number;
import java.lang.StringBuilder;
import java.text.ParseException;
import java.lang.Character;
import duct.main.lang.ParseUtils;

public class DuctValue {

  public static enum Type {
    TEXT, NUMBER, BOOL, LIST, FUNCTION; 
    public static Type parseType(CharSequence extractedType) throws ParseException{
      for(Type t:Type.values()){
        String name = t.name();
        //instead of specifying the whole name of the type, one can specify the first letter of the type name 
        if( StringUtils.equalsIgnoreCase(name, extractedType) ||
            (extractedType.length() == 1 && StringUtils.startsWithIgnoreCase(name, extractedType) ))
          return t;
       }
     //if no type was found despite the presence of the ':' then error out. 
     throw new ParseException("Type specifier '"+extractedType+"' is not valid.", 0); 
    }
  }

  public static class ValueInitException extends IllegalArgumentException{
    public static final String BASE_MSG = "Error occurred while initalizing value: ";
    public ValueInitException(String s){ super(BASE_MSG + s); }
    public ValueInitException(String message, Throwable cause){ super(BASE_MSG + message, cause); }
    public ValueInitException(Throwable cause){ super(cause); }
  }
  
  public final Type type;
  protected Object value;

  public Object getValue(){
    return this.value;
  }

  protected DuctValue(){
    this.type = Type.TEXT;
    this.value = null;
  }

  public DuctValue(Type t, CharSequence val) throws ValueInitException, ParseException{
    this.type = t;
    String inputValue = val.toString();
    switch(t){
      case TEXT:     this.value = inputValue; break;
      case NUMBER:   this.value = toNumber(inputValue); break;
      case BOOL:     this.value = toBool(inputValue); break;
      case LIST:     this.value = toList(inputValue); break;
      case FUNCTION: this.value = toFunction(inputValue); break;
    }
  }

  public DuctValue(DuctValue d){
    this.type = d.type;
    this.value = d.getValue();
  }

  public String toString(){
    return value.toString();
  }
    
  public boolean equals(DuctValue obj){
    return obj.type == this.type && this.value.equals(obj.getValue());
  }
  /**
   * Interprets the string value as a number.
  **/
  public static Number toNumber(String value) throws ValueInitException{
    Number n = null;
    try{
      n = Long.parseLong(value);
    }catch(NumberFormatException nfe){
      try{
        n = Double.parseDouble(value);
      }catch(NumberFormatException nfe2){
        throw new ValueInitException("Unable to interpret number from given text '"+value+"'.");
      }
    }
  return n;
  }

  public static List<String> boolStringValues = Arrays.asList("true", "false");
  /**
   * Interprets the string value as a boolean. 
   * @return If the value is either 'true' or 'false' it returns that value else,
   *    an exception is thrown.
  **/
  public static Boolean toBool(String value) throws ValueInitException{
    value = StringUtils.trimToEmpty(value).toLowerCase();
    if(!boolStringValues.contains(value))
      throw new ValueInitException("Value given '"+value+"' is not a boolean value."); 

  return Boolean.parseBoolean(value);
  }

  private static final String valueNotEnclosedMessage = "Value not properly enclosed in '<', '>'!";

  /**
   * Function to parse ductvalue from a raw CharSequence. This has the advantage of running dynamic cade more easily.
   * Syntax: \<<type>:<value>\> | \<:<value>\> | list:\<<type>:<value>\> \<<type>:<value>\>...
  **/
  public static DuctValue parseDuctValue(CharSequence value) throws ParseException{
    //make sure that the value is enclosed, otherwise syntax is not valid
    if(value.charAt(0) != '<')
      throw new ParseException(valueNotEnclosedMessage + " Value needs to open with '<'.", 0);
    
    if(value.charAt(value.length()-1) != '>')
      throw new ParseException(valueNotEnclosedMessage, value.length());

    if( value.charAt(value.length()-2) == '\\') 
      throw new ParseException(valueNotEnclosedMessage+" Last '>' found was escaped.", value.length()-2);

    //get the first colon that isn't escaped
    int indexOfColon = ParseUtils.indexOfUnescaped(value, ':');

    //the type is assumed to be text by default. 
    Type t = Type.TEXT;
       
    CharSequence extractedValue = value.subSequence(indexOfColon+1, value.length()-1);
     
    if(indexOfColon > 1){
      String extractedType = value.subSequence(1, indexOfColon).toString();
      t = Type.parseType(extractedType);
    }
    
    DuctValue d;
    try{
      d = new DuctValue(t, extractedValue);
    }catch(ParseException p){
      throw new ParseException(p.getMessage(), p.getErrorOffset()+indexOfColon);
    }
  return d; 
  }

  public static List<DuctValue> toList(CharSequence value) throws ValueInitException, ParseException{
    List<DuctValue> listValue = new ArrayList<DuctValue>();
    int start = 0;
    while(value.length() > start){
      //the only thing allowed in this sequence: '<', '>' and chars between '<' and '>' any whitespace can be ignored
      char curChar = value.charAt(start);
      if(curChar == '<') {
        int end = ParseUtils.indexOfUnescaped(value, '>', start) + 1;
        //try to parse the individual value, adding to the offset of the parseException should one occur
        try{
          listValue.add(parseDuctValue(value.subSequence(start, end)));
        } catch(ParseException p){
          throw new ParseException(p.getMessage(), p.getErrorOffset()+start);
        }
        start = end;
      //ignore spaces
      } else if(Character.isWhitespace(curChar)){
        start++;
      } else {
        throw new ParseException("Invalid character '"+ curChar + "' while parsing list.", start);
      } 
      
    }

    return listValue;
  }

  public static List<String> toFunction(String val) throws ValueInitException{
    return new ArrayList<String>();
  }
}
