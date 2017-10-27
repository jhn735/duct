package duct.main;
import org.apache.commons.cli.*;
import java.lang.System;
import duct.main.cli.DuctCLIArgument;
import duct.main.cli.CLIProcessor;
import duct.config.DuctInstanceConfig;
import java.util.Arrays;
import java.util.List;
/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{

  public static void main(String[] args){
		System.out.println("hello world!!");
    try{
 
      Duct d = new Duct(Arrays.asList(args));
    } catch (UnrecognizedOptionException u){
      System.out.println("Error: Unknown argument '" + u.getOption() +"'");
    }catch (ParseException p){
       
    }
  }

  protected DuctInstanceConfig config;
  
  public Duct(List<String> args) throws ParseException{
    CLIProcessor proc = new CLIProcessor(args); 
  }

  public void run(){
  }

}
