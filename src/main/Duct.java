package duct.main;
import org.apache.commons.cli.*;
import java.lang.System;
import duct.main.cli.DuctCLIArgument;
import duct.main.cli.DuctCLIArgument.ArgDef;
import duct.main.cli.CLIProcessor;
import duct.main.lang.DuctValue;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.Long;
import java.lang.Number;
import java.util.Scanner;
/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{
  public static enum ConfigProperty {
    SCRIPT_PATH, START_REPL, DISP_HELP;
  };
 
  protected Map<String, Object> config; 
 
  public static void main(String[] args){
		System.out.println("hello world!!");
    Boolean b = new Boolean(" not false ");

    System.out.println(b.toString());
    //String s = "4235+4569";
    //Object a = Long.parseLong(s);


    Scanner s = new Scanner(System.in);
    String val = "";
    try{
      do{
        val = s.nextLine();
        DuctValue d = DuctValue.parseDuctValue(val);
        System.out.println(val);
        System.out.println(d.type.name()); 
      }while(true);
    }catch(java.text.ParseException e){
      System.out.println(e.getMessage());     
      System.out.println(e.getErrorOffset());     
      System.out.println(val.charAt(e.getErrorOffset()));     

    }
    //if(a instanceof Double)
    //  System.out.println("THis value is a not long!!!");
    //System.out.println(a.getClass()); 
    //System.out.println(a);
  }

  public Duct(List<String> args){
    config = new HashMap<String, Object>(); 
    CLIProcessor proc = new CLIProcessor(args);
    for(DuctCLIArgument arg:proc.arguments){
      switch(arg.definition){
        case FILE: {
          config.put(ConfigProperty.SCRIPT_PATH.name(), arg.value);
        break;
        } 
        case INTERACTIVE: {
          config.put(ConfigProperty.START_REPL.name(), new Boolean(true));
        break;
        }
        default:{
         config.put(ConfigProperty.DISP_HELP.name(), new Boolean(true)); 
        }
      }
    } 
  }

  public void run(){
    //if interactive flag is set, run the repl
    
  }

}
