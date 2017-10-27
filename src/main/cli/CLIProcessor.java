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
  public final Set<DuctCLIArgument> ductArgs;
  public final List<String> scriptArgs;

  public CLIProcessor(List<String> arguments) throws ParseException{
    CommandLine cmd = parseArgs(arguments);
    this.ductArgs = extractArgs(cmd);
    this.scriptArgs = cmd.getArgList(); 
  }

  /**
   * Extracts the arguments parsed, sets member values where appropriate and returns a set of dArgs to take. 
   * @param cmd The object holding the parse commandline arguments.
   * @return A set of dArgs to take.
   */
  private static Set<DuctCLIArgument> extractArgs(CommandLine cmd){
    HashSet<DuctCLIArgument> dArgs = new HashSet<>();
    
    //gather all the arguments that have been specified given process them.
    for(ArgDef dArg:ArgDef.values()){
      //pick the right name, add it to the set and... 
      if(cmd.hasOption(dArg.name)){
        String argumentValue = cmd.getOptionValue(dArg.name, "");
        dArgs.add(new DuctCLIArgument(dArg, argumentValue));
      }
    }
    
  return dArgs;
  }

  /**
   * Contains boilerplate to parse arguments with apache commons-cli lib.
   * @param args The arguments to parse.
   * @return An interface with which to access the results of parsing.
   */
  private static CommandLine parseArgs(List<String> args) throws ParseException{
		Options options = new Options();

    //for each Duct Argument defined in enumeration, create an option.
    for(ArgDef dArg:ArgDef.values())
      options.addOption(dArg.shortName, dArg.name, dArg.argumentRequired, dArg.description);

    CommandLineParser parser = new DefaultParser();
  return parser.parse(options, args.toArray(new String[0]), true);
  }
} 
