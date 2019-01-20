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
		super( "Logger", constructOperations( exe ), exe, null );
	}

	protected abstract static class LogMsg extends Operation {
		protected static URL logsLocation = null;
		protected final ReentrantLock Lock = new ReentrantLock( true );
		protected static BufferedWriter logWriter = null;

		private static final String LOG_LOCATION_ERR_MSG =
			"Unable to initialize logs location. URL is either already reserved or the interpreter is unable to reserve the URL.";
		public LogMsg( CharSequence name, Executor exe ){
			super( name );

			if( lock.tryLock( 1, TimeUnit.SECONDS ) ){
				try{
					if( logsLocation == null ){
						logsLocation = exe.requestJurisdictionURL( "logs" );
						//if the returned value is a null, something went wrong with the request or the url has already been taken.
						if( logsLocation == null )
							throw new IllegalStateException( LOG_LOCATION_ERR_MSG );

						logWriter = new BufferedWriter( new FileWriter( createLogFile( "log", logsLocation ) ) );
					}
				} finally {
					lock.unlock();
				}
			}
		}

		protected static File createLogFile( String label, URL FileURL ){
			return null;
		}
	}

	protected static class Log extends LogMsg {
		public Log( Executor exe ){
			super( "Log", exe );
		}

		public Value doOperation( List<Value> operands ){
			List<Value> nOperands = new ArrayList<>( operands );
			//this operation takes at least two values. Fill them in if there aren't enough.
			while ( nOperands.size() < 2 ){
				nOperands.add( Value.defaultValue(Type.TEXT) );
			}

		return nOperands.get( 1 );
		}
	}

	@Override
	public Operation displayHelp(){
		return null;
	}

	private static Set<Operation> constructOperations( Executor exe ){
		return new HashSet<Operation>( Arrays.asList( new Log( exe ) ) );
	}
}
