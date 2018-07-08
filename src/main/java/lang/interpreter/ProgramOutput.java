package duct.main.lang.interpreter;

import duct.main.lang.Value;
import java.lang.Appendable;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.IOException;

public class ProgramOutput extends InterpreterAgent {

	public static enum DisplayMode {
		FULL, LOGGER, BASIC;
	}
	
	protected Set<Appendable> outputOutlets;
	protected DisplayMode mode;

	public ProgramOutput( URL jurisdictionURL ){
		this( jurisdictionURL, DisplayMode.FULL, constructDefaultOutputOutletSet( jurisdictionURL ) );
	}

	public ProgramOutput( URL JurisdictionURL, DisplayMode mode, Set<Appendable> outputs ) {
		super( JurisdictionURL, "ProgramOutput" );
		this.outputOutlets = outputs;
		this.mode       = mode;
	}

	public void setDisplayMode( DisplayMode mode ){
		this.mode = mode;
	}

	private static final String OUTPUT_FIELD_SEPARATOR = ", ";
	protected void printOutput( Collection<Appendable> outputs, String label, LocalDateTime dateTime, duct.main.lang.Type t, String value ) throws IOException {
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
		}
	}

	protected static Appendable appendText(Appendable a, String text ) throws IOException {
		return a.append('"').append(text).append('"').append( OUTPUT_FIELD_SEPARATOR );
	}

	@Override
	public Object handle( Object o ) {
		try {
			printOutput( this.outputOutlets, "", LocalDateTime.now(), null, o.toString() );
		} catch ( IOException io) {
		}
	return null;
	}

	public Object handle( Value v ) {
		return handle( v, "");
	}

	public Object handle( Value v, String label ) {
		return handle( v, label, LocalDateTime.now() );
	}

	public Object handle( Value v, String label, LocalDateTime datetime ) {
		try{
			printOutput( this.outputOutlets, label, datetime, v.type, v.toString() );
		} catch( IOException io ) {
		}
	return null;
	}

	private static Set<Appendable> constructDefaultOutputOutletSet( URL jurisdictionURL ) {
		Set<Appendable> outputOutletSet = new HashSet<Appendable>();
		outputOutletSet.add( java.lang.System.out );
	return outputOutletSet;
	}
}
