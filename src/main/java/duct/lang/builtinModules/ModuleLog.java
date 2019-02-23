package duct.lang.builtinModules;
import duct.lang.Executor;
import duct.lang.Operation;
import duct.lang.module.Module;
import duct.lang.value.type.Type;
import duct.lang.value.Value;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
  * Built in module which is responsible for logging messages at runtime.
  * For now the only operation supported is the help operation.
 **/
public class ModuleLog extends Module {
	public ModuleLog( Executor exe ){
		super( "Logger", constructOperations( exe ), exe, null );
	}

	abstract static class LogMsg extends Operation {
		static URL logsLocation = null;
		final ReentrantLock lock = new ReentrantLock( true );
		static BufferedWriter logWriter = null;

		private static final String LOG_LOCATION_ERR_MSG =
			"Unable to initialize logs location. URL is either already reserved or the interpreter is unable to reserve the URL.";
		LogMsg( CharSequence name, Executor exe ){
			super( name );

			try {
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
			} catch( InterruptedException|IOException e ){
				e.printStackTrace();
			}
		}

		static File createLogFile(String label, URL FileURL ){
			return null;
		}
	}

	protected static class Log extends LogMsg {
		Log( Executor exe ){
			super( "Log", exe );
		}

		public Value doOperation(List<Value> operands ){
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
		return new HashSet<>( Arrays.asList( new Log( exe ) ) );
	}
}
