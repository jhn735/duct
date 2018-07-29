package duct.main.lang.builtinModules;
import duct.main.lang.Executor;
import duct.main.lang.Module;
import duct.main.lang.Operation;
import duct.main.lang.Value;
import duct.main.lang.Type;
import java.util.List;
import java.util.Collection;

/**
  * Built in module which is responsible for logging messages at runtime.
  * For now the only operation supported is the help operation.
 **/
public class ModuleLog extends Module {

	public ModuleLog( Executor exe ){
		super("Logger", null, exe, null);
	}

	private abstract class LogMsg extends Operation {
		public LogMsg( CharSequence name ){
			super( name, ModuleLog.this );
		}
	}

	private class Log extends LogMsg {
		public Log(){
			super("Log");
		}
		
		public Value doOperation( List<Value> operands ){
			//this operation takes at least two values. Fill them in if there aren't enough.
			while ( operands.size() < 2 ){
				operands.add( Value.defaultValue(Type.TEXT) );
			}

			module.executor.displayValue( operands.get(1), operands.get(0).toString() );
		return operands.get(1);
		}
	}
	@Override
	public Operation displayHelp(){
		return null;
	}

	private static Collection<Operation> constructOperations( Module m ){
		return null;
	}
}
