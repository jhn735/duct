package duct.main;
import java.lang.System;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.cli.*;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{
  private String scriptFilename = "";

  private static enum Action {
    FILE("file", true, "Executes the given script file."), 
    HELP("help", false, "Displays a list of options and their descriptions."), 
    INTERACTIVE("interactive", false, "Starts the program in interactive mode. This is active by default.");
    final public String shortName;
    final public String name;
    final public boolean argumentRequired;
    final public String description;
    private Action(String name, boolean argRequired, String description){
      this.name = name;
      //for now the short name is simply the first letter of the name.
      this.shortName = Character.toString(this.name.charAt(0));
      this.argumentRequired = argRequired;
      this.description = description;
    }
  };
  
  private Set<Action> actions;
  
  public Duct(){
    
  }

  public Duct(String[] args){
    this.actions = new HashSet<Action>();
   try{
     CommandLine cmd = parseArgs(args);
     this.actions.addAll(processArgs(cmd));
   } catch(ParseException p){
     this.actions.clear();
     this.actions.add(Action.HELP);
   }
  }

  private List<Action> processArgs(CommandLine cmd){
    return new ArrayList<Action>();
  }

  public void run(){
  }

  public static void main(String[] args){
		System.out.println("hello world!!");
    Duct d = new Duct(args);
  }
  
  private static CommandLine parseArgs(String[] args) throws ParseException{
		Options options = new Options();

    //for each Action, add an option where the short option name is the first letter of the name.
    for(Action action:Action.values())
      options.addOption(action.shortName, action.name, action.argumentRequired, action.description);

    //add any other options here that don't involve taking explicit action.
    //...

    CommandLineParser parser = new DefaultParser();
  return parser.parse(options, args);
  }
}
