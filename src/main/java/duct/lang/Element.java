package duct.lang;


/**
	* A class to represent any functional element in the duct language.
 **/
public abstract class Element{
	protected final String name;

	public Element( CharSequence name ){
		this.name = name.toString();
	}

	//A horse with no name.
	public Element(){
		this.name = "";
	}

	public final boolean hasName(){
		return this.name != null && !this.name.isEmpty();
	}

	public final boolean hasSameName( Element e ){
		return this.name.equalsIgnoreCase( e.name );
	}

	public final String getName(){
		return this.name;
	}
}
