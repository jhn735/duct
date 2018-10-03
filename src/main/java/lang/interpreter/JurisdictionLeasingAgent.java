package duct.main.lang.interpreter;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.lang.IllegalArgumentException;

public class JurisdictionLeasingAgent extends InterpreterAgent {
	private static final String JURISDICTION_AGENT_NAME = "JurisdictionLeaseAgent";

	private Map<String, URL> leasedURLs;

	public JurisdictionLeasingAgent( URL jurisdictionDirectory ){
		super( jurisdictionDirectory, JURISDICTION_AGENT_NAME );
		leasedURLs = new HashMap<String, URL>();
	}

	/**
	  * Leases a directory with the given name representing a jurisdiction leased.
	  * @param jurisdictionName The name of the jurisdiction to lease.
	  * @return A URL representing the requested jurisdiction or null if such a URL has already been leased.
	 **/
	public URL lease( String jurisdictionName ){
		Pattern alphaNumOnly = Pattern.compile("[^a-z0-9 /]", Pattern.CASE_INSENSITIVE);
		Matcher ma = alphaNumOnly.matcher( jurisdictionName );

		if( !ma.find() && !this.leasedURLs.containsKey( jurisdictionName ) ){
			return this.addNewLeasedURL( this.jurisdictionDir, jurisdictionName );
		}
		return null;
	}

	private URL addNewLeasedURL( URL baseURL, String jurisdictionName ){
		try{
			URL newURL = new URL( baseURL, jurisdictionName );
			if( !this.leasedURLs.containsValue( newURL ) ){
				InterpreterAgent.createDirectory( newURL );
				this.leasedURLs.put( jurisdictionName, newURL );
				return newURL;
			}
		} catch( MalformedURLException m ){
			//log this error
		} catch( URISyntaxException u ){
			//log this error
		} catch( IllegalArgumentException i ){
			//log this error
		}
		return null;
	}

	public Object handle( String jurisdictionName ){
		return lease( jurisdictionName );
	}
}
