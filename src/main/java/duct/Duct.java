package duct;
import java.lang.System;
import duct.cli.cli.DuctCLIArgument;
import duct.cli.cli.CLIProcessor;
import duct.lang.interpreter.DuctLangInterpreter;
import duct.lang.interpreter.ProgramOutput;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class loads everything that needs loading and brings up a repl when not
 * disabled by user arguments. 
*/
public class Duct{
	public static enum ConfigProperty {
		SCRIPT_PATH, START_REPL, DISP_HELP
	}

	protected Map<String, Object> config;

	public static void main(String[] args){
	}

	public Duct(List<String> args){
		config = new HashMap<String, Object>();
		CLIProcessor proc = new CLIProcessor(args);
		for(DuctCLIArgument arg:proc.arguments){
			switch(arg.definition){
				case FILE:
					config.put(ConfigProperty.SCRIPT_PATH.name(), arg.value);
				break;

				case INTERACTIVE:
					config.put(ConfigProperty.START_REPL.name(), Boolean.TRUE);
				break;

				default:
				 config.put(ConfigProperty.DISP_HELP.name(), Boolean.TRUE);
			}
		}
	}

	public void run(){
		//if interactive flag is set, run the repl
		Scanner s = new Scanner(System.in);
		String val = "";

		DuctLangInterpreter interpreter = new DuctLangInterpreter();
		ProgramOutput po;

		while( !interpreter.terminated() ){
			CharSequence nextStatement = s.nextLine();
			interpreter.interpretStatement(nextStatement);
		}

	}

}
