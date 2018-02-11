package duct.main;
import org.apache.commons.cli.*;
import java.lang.System;
import duct.main.cli.DuctCLIArgument;
import duct.main.cli.DuctCLIArgument.ArgDef;
import duct.main.cli.CLIProcessor;
import duct.main.lang.Value;
import duct.main.lang.Module;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.Long;
import java.lang.Number;
import java.util.Scanner;
import java.io.StringReader;
import org.apache.commons.lang3.StringUtils;
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
    Scanner s = new Scanner(System.in);
    String val = "";

    try{
      for(int i = 0; i<5; i++){
        val = s.nextLine();
        StringReader r = new StringReader(val);
        Value d = Value.nextValue(r);
        System.out.println(d.type.name()); 
        System.out.println(d.name);
      }
    }catch(java.text.ParseException e){
      System.out.println(e.getMessage());     
      System.out.println(e.getErrorOffset());     
      System.out.println(val);
      System.out.println(StringUtils.repeat(' ', e.getErrorOffset()) + "^");
    }catch(java.io.IOException i){
      System.out.println(i.getMessage());
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
