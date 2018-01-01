package duct.main.lang;
import java.lang.Long;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Long;
import java.lang.Double;
import java.lang.CharSequence;
import org.apache.commons.lang3.StringUtils;
import java.lang.Number;
import java.lang.StringBuilder;
import java.text.ParseException;
import java.lang.Character;
import duct.main.lang.ParseUtils;
import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;

public class DuctValue {

  public static enum Type {
    TEXT, NUMBER, BOOL, LIST, SCRIPT, SET; 
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

  public static class ValueInitException extends ParseException{
    public static final String BASE_MSG = "Error occurred while initalizing value: ";
    public ValueInitException(String s, int errorOffset){ super(BASE_MSG + s, errorOffset); }
  }
  
  public final Type type;
  protected Object value;
  public final String name;

  public Object getValue(){
    return this.value;
  }

  protected DuctValue(){
    this.type = Type.TEXT;
    this.name = "";
    this.value = null;
  }

  public DuctValue(Type t, CharSequence name, CharSequence val) throws ParseException{
    this.type = t;
    
    this.name = (name == null)?"":name.toString();
    switch(t){
      case TEXT:     this.value = interpretString(val); break;
      case NUMBER:   this.value = interpretNumber(val); break;
      case BOOL:     this.value = interpretBool(val);   break;
      case LIST:     this.value = interpretList(val);   break;
      case SET:      this.value = interpretSet(val); break;      
      case SCRIPT:   this.value = interpretScript(val); break;
      default: throw new ParseException("Cannot interpret value with type given." , 0);
    }
  }
  
  public DuctValue(DuctValue d){
    this.type = d.type;
    this.value = d.getValue();
    this.name = d.name;
  }

  public String toString(){
    return value.toString();
  }
    
  public boolean equals(DuctValue obj){
    return obj.type == this.type && this.value.equals(obj.getValue());
  }
  
  public static String interpretString(CharSequence value) {
    return StringUtils.remove(value.toString(), '\\');
  }

  /**
   * Interprets the string value as a number. It first tries to interpret as a long and then as a double.
  **/
  public static Number interpretNumber(CharSequence value) throws ValueInitException{
    Number n = null;
    try{
      n = Long.parseLong(value.toString());
    }catch(NumberFormatException nfe){
      try{
        n = Double.parseDouble(value.toString());
      }catch(NumberFormatException nfe2){
        throw new ValueInitException("Unable to interpret number from given text '"+value+"'.", 0);
      }
    }
  return n;
  }

  public static List<CharSequence> boolStringValues = Arrays.asList("true", "false");
  /**
   * Interprets the string value as a boolean. 
   * @return If the value is either 'true' or 'false' it returns that value else,
   *    an exception is thrown.
  **/
  public static Boolean interpretBool(CharSequence value) throws ValueInitException{
    value = StringUtils.trimToEmpty(value.toString()).toLowerCase();
    if(!boolStringValues.contains(value))
      throw new ValueInitException("Value given '"+value+"' is not a boolean value.", 0); 

  return Boolean.parseBoolean(value.toString());
  }

  private static final String SRC_READ_ERROR_MSG = "Unabe to read source";

  /*
   * Interprets the char sequence value as a list of Duct Values. Values are accessed from a list by calling it as a function with the index passed as a parameter.
   * @return A list a Duct values if one exists
  */
  public static List<DuctValue> interpretList(CharSequence value) throws ValueInitException, ParseException{
    List<DuctValue> listValue = new ArrayList<DuctValue>();
    try{
      StringReader r = new StringReader(value.toString());
      r.mark(0);
      //while the reader isn't empty
      while(r.read() >= 0 ){
        r.reset();

        listValue.add(nextDuctValue(r));       
        r.mark(0);
      }
    } catch(IOException i){
      throw new ParseException( SRC_READ_ERROR_MSG, 0);
    }
    
  return listValue;
  }

  /*
   * Interprets the char sequence value as a set of named duct values. Accessing values from a set is similar to accessing values from a list, only rather than an index, a name is given.
   * @return A set of named duct values if one exists
  */
  public static Map<String, DuctValue> interpretSet(CharSequence value) throws ValueInitException, ParseException {
    Map<String,DuctValue> set = new HashMap<String, DuctValue>();

    //the result of 'interpretList' will always be a super set of the result of 'interpretSet'
    List<DuctValue> valueList = interpretList(value);

   //if the value has no name then the value can't be accessed so there is no point in adding it to the set.
    for(DuctValue d:valueList)
      if(!StringUtils.isEmpty(d.name))
        set.put(d.name, d);

  return set;
  }

  private static final String VALUE_NOT_ENCLOSED_MSG = "A value must open with '<' and close with '>'";
  private static final String PREMATURE_END_OF_STREAM_MSG = "The passed in value ended prematurely";
  private static final String IDENTIFIER_ORDER_ERR_MSG = "The name or identifier of a value must be specified before it's type is specified.";
  public static DuctValue nextDuctValue(Reader reader) throws ParseException, IOException {
    StringBuilder extractedValue = new StringBuilder();
    //make sure to get number of charecters read, in case a parse exception is thrown.
    int charCount = 0;
    char curChar = readNextChar(reader);
    charCount++;
    //throw a fit if the value is not started properly
    if(curChar != '<')
      throw new ParseException(VALUE_NOT_ENCLOSED_MSG, charCount);

    int valueStart = charCount;

    //working on the assumption that the first '<' is there. otherwise an exception would have been thrown at this point.
    long enclosureDepth = 1;
    Type type = null;
    String name = null;

    //Read character loop, calling continue simple means skipping to the next character.
    do{
      curChar = readNextChar(reader);
      charCount++;
      
      switch(curChar){
        //this indicates that the value is named something. The name of the value can only be specified before the type is specified, hence the null check.
        case '#':
          if(type == null){
            name = extractedValue.toString();
            extractedValue.setLength(0);
            continue;
          //if the the type is text then we can assume that the '#' is a part of the text value. Otherwise an error has occurred.
          } else if(type != Type.TEXT ) {
            throw new ParseException( IDENTIFIER_ORDER_ERR_MSG, valueStart);
          }
        break;
        //we only need to worry about matching pairs of '<' and '>' in lists and scripts.
        case '<': 
          if(type == Type.LIST || type == Type.SCRIPT || type == Type.SET)
            enclosureDepth++; 
        break;
        case '>': 
          enclosureDepth--; 
        break;
        case ':': { 
          //if the type has not been extracted, try to parse the type with the characters gathered and 'clear' them.
          if(type == null){
            try{
             type = Type.parseType(extractedValue);
            }catch (ParseException p){
             throw new ParseException(p.getMessage(), p.getErrorOffset() + valueStart);
            }
            
            extractedValue.setLength(0);
            valueStart = charCount+1;
          //moving on to the next character.
          continue; 
          }
        break;
        }
        case '\\': {
          extractedValue.append(curChar);
          curChar = readNextChar(reader); 
          charCount++;
        break;
        }
      }
      //we want to capture everything except the outermost '<' and '>'
      if(enclosureDepth > 0) 
        extractedValue.append(curChar);
    } while(enclosureDepth > 0);
    
    if(type == null)
        throw new ParseException("A type must be specified for each value.", valueStart);

    DuctValue d = null;

    try{ 
      //Attempt to parse the value having parsed the type and extracted the name and the string value.  
      d = new DuctValue(type, name, extractedValue);
    }catch(ParseException p) {
      throw new ParseException(p.getMessage(), p.getErrorOffset() + valueStart);
    }
  return d;
  }     
  
  private static char readNextChar(Reader reader) throws IOException, ParseException{
    int curChar = reader.read();
    if(curChar < 0)
      throw new ParseException(PREMATURE_END_OF_STREAM_MSG, 0);
  return (char)curChar;
  }

  /**
   * Saves the value as a string, parsing later upon execution.
   * @return The string value of the value given.
  **/
  public static String interpretScript(CharSequence value) throws ValueInitException{
    
    return value.toString();
  }
}
