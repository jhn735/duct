package duct.main.lang.builtinModules;
import duct.main.lang.Executor;
import duct.main.lang.Module;
import duct.main.lang.Operation;
import duct.main.lang.Value;
import duct.main.lang.Type;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;

/**
  * Built in module which is responsible for logging messages at runtime.
  * For now the only operation supported is the help operation.
 **/
public class ModuleLog extends Module {
	public ModuleLog( Executor exe ){
		super( "Logger", constructOperations(), exe, null );
	}

	protected abstract static class LogMsg extends Operation {
		public LogMsg( CharSequence name ){
			super( name );
		}
	}

	protected static class Log extends LogMsg {
		public Log(){
			super( "Log" );
		}

		public Value doOperation( List<Value> operands ){
			List<Value> nOperands = new ArrayList<>( operands );
			//this operation takes at least two values. Fill them in if there aren't enough.
			while ( nOperands.size() < 2 ){
				nOperands.add( Value.defaultValue(Type.TEXT) );
			}

			module.executor.displayValue( nOperands.get(1), nOperands.get(0).toString() );
		return nOperands.get( 1 );
		}
	}

	@Override
	public Operation displayHelp(){
		return null;
	}

	private static Set<Operation> constructOperations(){
		return new HashSet<Operation>( Arrays.asList( new Log() ) );
	}
}
