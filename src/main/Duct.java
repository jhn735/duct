package duct.main;
import java.lang.System;
import org.apache.commons.lang3.StringUtils;

/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{

  public Duct(){
    
  }

  public Duct(String[] args){
    parseArgs(args);
  }

  public static void main(String[] args){
		System.out.println("hello world!!");
    Duct d = new Duct(args);
  }
  
  private void parseArgs(String[] args){
    String arg;
    for(int i = 0; i < args.length; i++){
			arg = args[i];
      
      System.out.println(StringUtils.trimToEmpty(args[i]));
		}
  }
}
