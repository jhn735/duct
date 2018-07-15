package duct.main.lang.interpreter;

import duct.main.lang.Value;
import java.lang.Appendable;
import java.lang.System;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.ZonedDateTime;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class ProgramOutput extends InterpreterAgent {

	public static enum DisplayMode {
		FULL, LOGGER, BASIC;
	}

	protected Set<Appendable> outputOutlets;
	protected DisplayMode mode;

	public ProgramOutput( URL jurisdictionURL ){
		super( jurisdictionURL, "ProgramOutput" );
		this.outputOutlets = constructDefaultOutputOutletSet( jurisdictionURL );
		this.mode          = DisplayMode.FULL;
	}

	public ProgramOutput( URL JurisdictionURL, DisplayMode mode, Set<Appendable> outputs ) {
		super( JurisdictionURL, "ProgramOutput" );
		this.outputOutlets = outputs;
		this.mode          = mode;
	}

	public void setDisplayMode( DisplayMode mode ){
		this.mode = mode;
	}

	private static final String OUTPUT_FIELD_SEPARATOR = ", ";
	protected void printOutput( Collection<Appendable> outputs, String label,
	 TemporalAccessor dateTime, duct.main.lang.Type t, String value ) throws IOException {

		String date = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime);
		String type = (t == null )? "null" : t.name();

		for( Appendable output:outputs ) {
			if( this.mode != DisplayMode.BASIC ){
				appendText( output, label);
				output.append( date  ).append( OUTPUT_FIELD_SEPARATOR );
			}

			if( this.mode == DisplayMode.FULL ){
				appendText( output, type );
			}

			appendText( output, value);
			output.append( System.lineSeparator() );
		}
	}

	protected static Appendable appendText(Appendable a, String text ) throws IOException {
		return a.append('"').append(text).append('"').append( OUTPUT_FIELD_SEPARATOR );
	}

	@Override
	public Object handle( Object o ) {
		try {
			printOutput( this.outputOutlets, "", ZonedDateTime.now(), null, o.toString() );
		} catch ( IOException io) {
		}
	return null;
	}

	public Object handle( Value v ) {
		return handle( v, "");
	}

	public Object handle( Value v, String label ) {
		return handle( v, label, ZonedDateTime.now() );
	}

	public Object handle( Value v, String label, TemporalAccessor datetime ) {
		try{
			printOutput( this.outputOutlets, label, datetime, v.type, v.toString() );
		} catch( IOException io ) {
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

	private static Set<Appendable> constructDefaultOutputOutletSet( URL jurisdictionURL ) {
		Set<Appendable> outputOutletSet = new HashSet<Appendable>();
		outputOutletSet.add( java.lang.System.out );

		String logFileName = "duct.log." + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now());
		try {
			File logFileDir = new File(jurisdictionURL.toURI());
			File logFile    = new File(logFileDir, logFileName );
			logFile.createNewFile();

			outputOutletSet.add( new FileWriter(logFile) );
		} catch (URISyntaxException syn){
			throw new RuntimeException( "Unable to create log file.", syn);	
		} catch (IOException io ) {
			throw new RuntimeException( "Unable to create log file.", io);
		}
	return outputOutletSet;
	}
}
