package duct.lang.interpreter;

import duct.lang.value.Type;
import duct.lang.value.Value;
import java.lang.Appendable;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.ZonedDateTime;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;

public class ProgramOutput extends InterpreterAgent {

	public enum DisplayMode {
		FULL, LOGGER, BASIC
	}

	private Set<PrintStream> outputOutlets;
	private DisplayMode mode;

	public ProgramOutput( URL jurisdictionURL ){
		super( jurisdictionURL, "ProgramOutput" );
		this.outputOutlets = constructDefaultOutputOutletSet( jurisdictionURL );
		this.mode          = DisplayMode.FULL;
	}

	public ProgramOutput( URL JurisdictionURL, DisplayMode mode, Set<PrintStream> outputs ) {
		super( JurisdictionURL, "ProgramOutput" );
		this.outputOutlets = outputs;
		this.mode          = mode;
	}

	public void setDisplayMode( DisplayMode mode ){
		this.mode = mode;
	}

	private static final String OUTPUT_FIELD_SEPARATOR = ", ";
	private void printOutput(Collection<PrintStream> outputs, String label,
														 TemporalAccessor dateTime, Type t, String value ) throws IOException {

		String date = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime);
		String type = (t == null )? "null" : t.name();

		StringBuilder logLine = new StringBuilder();
		if( this.mode != DisplayMode.BASIC ){
			appendText( logLine, label);
			logLine.append( date  ).append( OUTPUT_FIELD_SEPARATOR );
		}

		if( this.mode == DisplayMode.FULL ){
			appendText( logLine, type );
		}

		appendText( logLine, value);

		for(PrintStream output:outputs )
			output.println(logLine.toString());
	}

	private static Appendable appendText( Appendable a, String text ) throws IOException {
		return a.append('"').append(text).append('"').append( OUTPUT_FIELD_SEPARATOR );
	}

	@Override
	public Object handle( Object o ) {
		try {
			printOutput( this.outputOutlets, "", ZonedDateTime.now(), null, o.toString() );
		} catch ( IOException io) {
			//handle exception
		}
	return null;
	}

	Object handle( Value v ) {
		return handle( v, "");
	}

	Object handle( Value v, String label ) {
		return handle( v, label, ZonedDateTime.now() );
	}

	Object handle( Value v, String label, TemporalAccessor datetime ) {
		try{
			printOutput( this.outputOutlets, label, datetime, v.type, v.toString() );
		} catch( IOException io ) {
			//handle Exception
		}
	return null;
	}

	@Override
	public void close() throws Exception {
		for( Appendable outlet:outputOutlets ){
			if( outlet instanceof java.lang.AutoCloseable )
				( (java.lang.AutoCloseable) outlet).close();
		}
	}

	private static Set<PrintStream> constructDefaultOutputOutletSet( URL jurisdictionURL ) {
		Set<PrintStream> outputOutletSet = new HashSet<>();
		outputOutletSet.add( java.lang.System.out );

		String logFileName = "duct.log." + DateTimeFormatter.ISO_LOCAL_DATE.format(ZonedDateTime.now());
		try {
			File logFileDir = new File(jurisdictionURL.toURI());
			File logFile    = new File(logFileDir, logFileName );
			logFile.createNewFile();

			outputOutletSet.add( new PrintStream( new FileOutputStream(logFile, true), true) );
		} catch (URISyntaxException| IOException e ){
			throw new RuntimeException( "Unable to create log file.", e);
		}
	return outputOutletSet;
	}
}
