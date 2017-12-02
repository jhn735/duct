package duct.main.lang;
import java.lang.Long;
import java.util.List;
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

  public static class ValueInitException extends ParseException{
    public static final String BASE_MSG = "Error occurred while initalizing value: ";
    public ValueInitException(String s, int errorOffset){ super(BASE_MSG + s, errorOffset); }
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

  public DuctValue(Type t, CharSequence val) throws ParseException{
    this.type = t;
    switch(t){
      case TEXT:     this.value = interpretString(val); break;
      case NUMBER:   this.value = interpretNumber(val); break;
      case BOOL:     this.value = interpretBool(val); break;
      case LIST:     this.value = interpretList(val); break;
      case FUNCTION: this.value = interpretFunction(val); break;
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
  
  public static String interpretString(CharSequence value) {
    return StringUtils.remove(value.toString(), '\\');
  }

  /**
   * Interprets the string value as a number.
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
    }
    
    return listValue;
  }

  private static final String valueNotEnclosedMessage = "A value must open with '<' and close with '>'";
  private static final String prematureEndOfStream = "The possed in value ended prematurely";
  public static DuctValue nextDuctValue(Reader reader) throws ParseException, IOException {
    StringBuilder extractedValue = new StringBuilder();
    //make sure to get number of charecters read, in case a parse exception is thrown.
    int charCount = 0;
    int valueStart = 0;
    char curChar = readNextChar(reader);
    charCount++;
    //throw a fit if the value is not started properly
    if(curChar != '<')
      throw new ParseException(valueNotEnclosedMessage, charCount);

    //working on the assumption that the first '<' is there. otherwise an exception would have been thrown at this point.
    long enclosureDepth = 1;
    //a flag used to determine whether or not the type was extracted.
    boolean typeExtracted = false;
    Type type = Type.TEXT;

    do{
      curChar = readNextChar(reader);
      charCount++;
      
      switch(curChar){
        //we only need to worry about matching pairs of '<' and '>' in lists and functions.
        case '<': 
          if(type == Type.LIST || type == Type.FUNCTION) 
            enclosureDepth++; 
        break;
        case '>': enclosureDepth--; break;
        case ':': { 
          //if the type has not been extracted, try to parse the type with the characters gathered and 'clear' them.
          if(!typeExtracted){
            try{
             type = Type.parseType(extractedValue);
            }catch (ParseException p){
             throw new ParseException(p.getMessage(), p.getErrorOffset() + charCount);
            }

            extractedValue.setLength(0);
            typeExtracted = true;
            valueStart = charCount+1;
          }
        continue; 
        }
        case '\\': {
          extractedValue.append(curChar);
          curChar = readNextChar(reader); 
          charCount++;
        break;
        }
      }
      //we want everything except the outermost '<' and '>'
      if(enclosureDepth > 0) 
        extractedValue.append(curChar);
    } while(enclosureDepth > 0);

    DuctValue d = null;

    try{ 
      //Attempt to parse the value having parsed the type and extracted the string value.  
      d = new DuctValue(type, extractedValue);
    }catch(ParseException p) {
      throw new ParseException(p.getMessage(), p.getErrorOffset() + valueStart);
    }
  return d;
  }     
  
  private static char readNextChar(Reader reader) throws IOException, ParseException{
    int curChar = reader.read();
    if(curChar < 0)
      throw new ParseException(prematureEndOfStream, 0);
  return (char)curChar;
  }

  //a function in duct is a script in and of itself. Running a function will spawn a new duct environment or process, I haven't decided yet.
  //methinks that a special object will be needed to acheive anything in the way of efficiency for function calls.
  public static List<String> interpretFunction(CharSequence val) throws ValueInitException{
    
    return new ArrayList<String>();
  }
}
