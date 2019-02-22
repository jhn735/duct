package duct.lang.interpreter;

import java.lang.UnsupportedOperationException;
import java.lang.AutoCloseable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.lang.IllegalArgumentException;

public abstract class InterpreterAgent implements AutoCloseable {

	public final URL jurisdictionDir;
	public final String name;

	public InterpreterAgent( URL jurisdictionDirectory, String agentName ){
		try{
			createDirectory( jurisdictionDirectory );
		} catch( URISyntaxException syn ){
			throw new RuntimeException( UNABLE_TO_CREATE_SUPPORTING_DIR_ERR_MSG + agentName + "." , syn );
		}

		this.name = agentName;
		this.jurisdictionDir = jurisdictionDirectory;
	}

	private static final String UNABLE_TO_CREATE_SUPPORTING_DIR_ERR_MSG =
		"Error occurred while creating supporting directories for the interpreter. Something went wrong with the construction of the URL for ";

	protected static void createDirectory( URL url ) throws URISyntaxException, IllegalArgumentException {
		File dir = new File( url.toURI() );
		dir.mkdirs();
		if( !dir.isDirectory() )
			throw new IllegalArgumentException( "Resource at URL '" + dir.toString() + "' must be a directory and not a file." );
	}

	/**
	  * Function where the agent is asked to handle input.
	  * @param o The input for this agent to handle.
	  * @return An object representing the output as a result of this agent's action if any.
	 **/
	public Object handle( Object o ){
		throw new UnsupportedOperationException( "This functionality has not been extended to handle this type of input." );
	}

	/**
		* An agent may have to handle external resources and may have tie up loose ends before a graceful shutdown.
	 **/
	@Override
	public void close() throws Exception {
		//do nothing for now.
	}
}
