package duct.main.lang.module;
import java.lang.CharSequence;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.lang.UnsupportedOperationException;

/**
 * Making the module an abstract and not an interface so that I can better control
 * how the application is extended. The executor is an interface because there is
 * less of a need to control how it does things vs a Module.
 * A module can be considered an immutable set of operations so I'm making it a set
 * backed by a HashSet.
**/
public abstract class Module extends OperationSet {
	private static final Charset DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8;

	public  final Executor executor;
	private final Map<String, byte[]> settings;

	public Module( CharSequence name, Collection<Operation> operations, Executor exe, Map<String, byte[]> settings ){
		super( name, new HashSet<Operation>( operations ) );
		//it's okay to not have initial settings. A module should be able to generate it's own defaults.
		if( settings != null )
			this.settings = new HashMap<String, byte[]>( settings );
		else
			this.settings = new HashMap<String, byte[]>();

		this.executor = exe;
	}

	public Module( CharSequence name, Collection<Operation> operations, Executor exe ){
		this( name, operations, exe, null );
	}

	/**
	  * Get the setting value stored under the key.
	  * @param key
	  * @return The value stored under the key in the settings map.
	 **/
	public byte[] getSetting( String key ){
		return this.settings.get( key );
	}

	public abstract Operation displayHelp();
}
