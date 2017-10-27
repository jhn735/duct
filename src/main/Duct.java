package duct.main;
import java.lang.System;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.cli.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import duct.main.cli.DuctCLIArgument;
import duct.config.DuctInstanceConfig;
/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{

  public static void main(String[] args){
		System.out.println("hello world!!");
    Duct d = new Duct(args);
  }

  protected DuctInstanceConfig config;
  
  private Set<DuctCLIArgument> dArgs;
  
  public Duct(){
    
  }

  public Duct(String[] args){
  }

  public void run(){
  }
 
}
