package duct.main.cli;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

public class DuctCLIArgument {
  public final ArgDef definition;
  /**
   * Enumeration contain the definitions of the arguments to pull.
   **/
  public static enum ArgDef{
    FILE("file", true, "Executes the given script file."), 
    HELP("help", false, "Displays a list of options and their descriptions."), 
    INTERACTIVE("interactive", false, "Starts the program in interactive mode. This is active by default.");

    final public String shortName;
    final public String name;
    final public boolean argumentRequired;
    final public String description;

    private ArgDef(String name, boolean argRequired, String description){
      this.name = name;
      //for now the short name is simply the first letter of the name.
      this.shortName = Character.toString(this.name.charAt(0));
      this.argumentRequired = argRequired;
      this.description = description;
    }
    public List<String> getNames(){
      return Arrays.asList(this.name, this.shortName);
    }
  }

  public DuctCLIArgument(Definition def){
    this.definition = def;
  }

};
