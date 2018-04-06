package duct.main.lang;

import duct.main.lang.ParseUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.Character;
import java.lang.Runnable;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.commons.lang3.StringUtils;

/**
  * An object to represent a script, interpreted and packaged to be run whenever.
 **/
public class Script extends Element implements Runnable{
  public final Executor executor;
  private final Set<Module> modules;
  private final List<Evaluable> instructions;

  public Script( CharSequence name, List<Evaluable> instructions, Executor exe, Set<Module> modules ){ 
    super(name);
    this.executor = exe;
    this.modules = modules;
    this.instructions = instructions;    
  }

  public void run(){
    for(Evaluable e:instructions)
      e.evaluate();
  }
  
  public static Script nextScript( Reader reader, Executor exe ) throws ParseException, IOException {
    //every script must start with the following: 
    // ~<ScriptName>~  
    String name = nextScriptName( reader );
    List<Evaluable> instructions = new ArrayList<>();
    Set<Module> modules = new HashSet<>();
    
  return new Script( name, instructions, exe, modules );
  }  

  private static String nextScriptName( Reader r ) throws ParseException, IOException {
    BufferedReader reader = new BufferedReader(r);
    String line;

    //skip through all empty lines
    do {
      line = StringUtils.trimToEmpty( reader.readLine() );
    } while ( line.isEmpty()); 


    int nameBegin = 2;
    int nameEnd = line.indexOf(">~");

    if( !line.startsWith("~<") || !line.endsWith(">~") ){
      throw new ParseException("Each script must begin with a script name specified by ~<scriptName>~", 0);
    }

  return line.substring(nameBegin, nameEnd); 
  }
}
