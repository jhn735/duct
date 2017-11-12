package duct.main.lang;
import org.apache.commons.lang3.StringUtils;

public class ParseUtils{
  //Static function class
  private ParseUtils(){}

  public static int indexOfUnescaped(CharSequence value, int searchChar, int startPos){
    //get the first colon that isn't escaped
    int index = StringUtils.indexOf(value, searchChar, startPos);
    while(index > 0 && value.charAt(index-1) == '\\')
      index = StringUtils.indexOf(value, searchChar, index +1);
  return index;
  } 

  public static int indexOfUnescaped(CharSequence value, int searchChar){
    return indexOfUnescaped(value,searchChar, 0);
  }
}
