package duct.main.cli;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.lang.NullPointerException;

public class DuctCLIArgument {
  public final ArgDef definition;

  //no need for a boolean value for the flag, the mere presence suffices.
  public final String value;
  protected static final String ERR_MSG_BASE = "Unable to process argument: ";
  private final String NULL_DEF_MSG=ERR_MSG_BASE+"argument definition is null";
  
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

  public DuctCLIArgument(ArgDef def, String argValue) throws NullPointerException{
    if(def == null) 
      throw new NullPointerException(NULL_DEF_MSG);

    this.definition = def;
    //if the value passed in is null, assign an empty string
    this.value = (argValue==null) ? "" : argValue
  }
  
  //override the hashCode so that hash collections will be based on the arg definition.
  @Override
  public int hashCode(){
    return definition.hashCode();
  }
};
