package duct.main;
import java.lang.System;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.cli.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import duct.main.cli.DuctCLIArgument;
/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{

  public static void main(String[] args){
		System.out.println("hello world!!");
    Duct d = new Duct(args);
  }

  private String scriptFilename = "";
  
  private Set<DuctCLIArgument> dArgs;
  
  public Duct(){
    
  }

  public Duct(String[] args){
    this.dArgs = new HashSet<DuctCLIArgument>();
   try{
     CommandLine cmd = parseArgs(args);
     this.dArgs.addAll(processArgs(cmd));
   } catch(ParseException p){
     this.dArgs.clear();
     this.dArgs.add(DuctCLIArgument.HELP);
   }
  }

  /**
   * Processes the arguments parsed, sets member values where appropriate and returns a set of dArgs to take. 
   * @param cmd The object holding the parse commandline arguments.
   * @return A set of dArgs to take.
   */
  private Set<DuctCLIArgument> processArgs(CommandLine cmd){
    HashSet<DuctCLIArgument> dArgs = new HashSet<>();
    
    //gather all the arguments that have been specified given process them.
    for(DuctCLIArgument dArg:DuctCLIArgument.values()){
      //pick the right name, add it to the set and... 
      for(String name:dArg.getNames()){
        if(cmd.hasOption(name)){
          dArgs.add(dArg);
          
          //...do more processing if needed. 
          if(dArg.argumentRequired){
            String argumentValue = cmd.getOptionValue(name);
            processArgumentValue(dArg, argumentValue);
          }
        }
      } 
    }
    
  return dArgs;
  }

  /**
   * Used to process arguments that have a non-boolean value associated.
   * @param dArg The dArg associated with the argument.
   * @param value The value associated with the argument.
   **/
  private void processArgumentValue(DuctCLIArgument dArg, String value){
    switch(dArg){
      case FILE: break;
      case HELP: break;
      case INTERACTIVE: break;
    }     
  }

  public void run(){
  }
 
  /**
   * Contains what is essentially boilerplate code to parse the arguments with apache commons-cli lib.
   * @param args The arguments to parse.
   * @return An interface with which to access the results of parsing.
   */
  private static CommandLine parseArgs(String[] args) throws ParseException{
		Options options = new Options();

    //for each Duct Argument defined in enumeration, create an option.
    for(DuctCLIArgument dArg:DuctCLIArgument.values())
      options.addOption(dArg.shortName, dArg.name, dArg.argumentRequired, dArg.description);

    CommandLineParser parser = new DefaultParser();
  return parser.parse(options, args);
  }
}
