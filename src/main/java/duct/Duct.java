package duct;
import java.lang.System;
import duct.cli.cli.DuctCLIArgument;
import duct.cli.cli.CLIProcessor;
import duct.lang.value.Value;
import duct.lang.interpreter.ProgramOutput;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.util.Scanner;
import java.io.StringReader;
import org.apache.commons.lang3.StringUtils;

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
		Scanner s = new Scanner(System.in);
		String val = "";

		ProgramOutput po;

		try {
			URL rootURL = new URL("file:/home/jvilla/.duct/logs");

			po = new ProgramOutput(rootURL);
			for(int i = 0; i<2; i++) {
				val = s.nextLine();
				StringReader r = new StringReader(val);
				Value d = Value.nextValue(r);
				po.handle(d);
			}
			po.close();
		} catch(java.text.ParseException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getErrorOffset());
			System.out.println(val);
			System.out.println(StringUtils.repeat(' ', e.getErrorOffset()) + "^");
			e.printStackTrace(System.out);
		} catch(java.io.IOException i) {
			System.out.println(i.getMessage());
			i.printStackTrace(System.out);
		} catch(Exception e ){
			e.printStackTrace(System.out);
		}
	}

	public Duct(List<String> args){
		config = new HashMap<String, Object>();
		CLIProcessor proc = new CLIProcessor(args);
		for(DuctCLIArgument arg:proc.arguments){
			switch(arg.definition){
				case FILE: {
					config.put(ConfigProperty.SCRIPT_PATH.name(), arg.value);
				break;
				}
				case INTERACTIVE: {
					config.put(ConfigProperty.START_REPL.name(), Boolean.TRUE);
				break;
				}
				default:{
				 config.put(ConfigProperty.DISP_HELP.name(), Boolean.TRUE);
				}
			}
		}
	}

	public void run(){
		//if interactive flag is set, run the repl

	}

}
