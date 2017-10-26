package duct.main.cli;
import java.lang.System;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.cli.*;
import duct.main.cli.DuctCLIArgument.ArgDef;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;


public class CLIProcessor {
  public CLIProcessor(){

  }

  /**
   * Extracts the arguments parsed, sets member values where appropriate and returns a set of dArgs to take. 
   * @param cmd The object holding the parse commandline arguments.
   * @return A set of dArgs to take.
   */
  private Set<DuctCLIArgument> extractArgs(CommandLine cmd){
    HashSet<DuctCLIArgument> dArgs = new HashSet<>();
    
    //gather all the arguments that have been specified given process them.
    for(ArgDef dArg:ArgDef.values()){
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
 
  /**
   * Contains what is essentially boilerplate code to parse the arguments with apache commons-cli lib.
   * @param args The arguments to parse.
   * @return An interface with which to access the results of parsing.
   */
  private static CommandLine parseArgs(String[] args) throws ParseException{
		Options options = new Options();

    //for each Duct Argument defined in enumeration, create an option.
    for(ArgDef dArg:ArgDef.values())
      options.addOption(dArg.shortName, dArg.name, dArg.argumentRequired, dArg.description);

    CommandLineParser parser = new DefaultParser();
  return parser.parse(options, args);
  }
} 
