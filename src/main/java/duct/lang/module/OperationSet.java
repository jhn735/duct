package duct.lang.module;
import duct.lang.Element;
import duct.lang.Operation;

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
public abstract class OperationSet extends Element implements Set<Operation> {
	protected Set<Operation> operations;

	public OperationSet( CharSequence name, Collection<Operation> operations ){
		super(name);
		this.operations = new HashSet<Operation>( operations );
	}

	@Override
	public boolean add( Operation e )
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll( Collection<? extends Operation> e )
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains( Object o ){
		return operations.contains(o);
	}

	@Override
	public boolean containsAll( Collection<?> c ){
		return operations.containsAll(c);
	}

	@Override
	public boolean isEmpty(){
		return operations.isEmpty();
	}

	@Override
	public Iterator<Operation> iterator(){
		return operations.iterator();
	}

	@Override
	public boolean remove( Object o )
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll( Collection<?> c )
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll( Collection<?> c )
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size(){
		return operations.size();
	}

	@Override
	public Object[] toArray(){
		return operations.toArray();
	}

	@Override
	public <T> T[] toArray( T[] a ){
		return operations.toArray(a);
	}

	@Override
	public void clear() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals( Object o ){
		if( o.getClass().equals( this.getClass() ) ){
			return this.name.equalsIgnoreCase( ((OperationSet)o).name );
		}

		return false;
	}
}
