package duct.lang;
import java.util.List;
import java.util.ArrayList;
import java.lang.Character;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;
import java.text.ParseException;

import duct.lang.value.Value;

/**
 * Represents a statement in the duct language which does something. That 
 * includes, creating values, calling values, executing scripts... Creating a 
 * class rather than a function responsible for parsing expressions allows one 
 * to run an expression without parsing the script again.
**/
//expressions must have one operation and a set of zero or more values on which the operation works on. 
public class Expression extends Element implements Evaluable {
	private static final String EXPRESSION_NOT_ENCLOSED_MSG = "An expression must be enclosed between '(' and ')'.";

	private Operation operation;
	private String operationReference;

	private List<Evaluable> evaluables;

	private Executor executor;

	public Expression( Operation op, List<Evaluable> evaluables ) {
		super();
		this.operation = op;
		this.evaluables = evaluables;
	}

	public Expression( String opReference, List<Evaluable> evaluables ){
		this(opReference, evaluables, null);
	}

	public Expression( String opReference, List<Evaluable> evaluables, Executor exe ){
		super();
		this.operationReference = opReference;
		this.evaluables = evaluables;
		this.executor = exe;
	}

	public List<Evaluable> evaluables(){
		return new ArrayList<Evaluable>( evaluables );
	}

	//create a list of values from the list of evaluables given in the constructor
	public List<Value> values() {
		List<Value> values = new ArrayList<>();
		for( Evaluable e:evaluables ){
			values.add(e.evaluate());
		}
	return values;
	}

	public Value evaluate() {
		if( this.executor == null ){
			throw new IllegalStateException( "Executor must be set in order to evaluate an expression." );
		}

		return this.evaluate( this.executor );
	}

	public Value evaluate( Executor exe ){
		return operation.execute( exe, this.values() );
	}

	public static Expression nextExpression( Reader reader ) throws ParseException, IOException {
		int charCount = 0;
		char curChar = ParseUtils.readNextChar( reader );
		PushbackReader pReader = new PushbackReader( reader );

		String opReference;

		List<Evaluable> evaluables = new ArrayList<>();

		//throw a fit of the expression is not started properly
		if( curChar != '(' ){
			throw new ParseException( Expression.EXPRESSION_NOT_ENCLOSED_MSG, charCount );
		}

		//assuming the basic stuff is out the way, get the name of the operation and then
		//get the operation.
		StringBuilder name = new StringBuilder();
		while( !Character.isWhitespace( curChar ) ){
			curChar = ParseUtils.readNextChar( pReader );
			charCount++;
			name.append( curChar );
		}

		opReference = name.toString();

		do{
			curChar = ParseUtils.readNextChar( pReader );
			charCount++;

			switch( curChar ){
				case '(':
					pReader.unread( curChar );
					evaluables.add( Expression.nextExpression( pReader ) );
				break;

				//if the character is a '<' get the value it's suppose to represent
				case '<':
					pReader.unread( curChar );
					evaluables.add( Value.nextValue( pReader ) );
				break;
				//Anything else should cause an error to be thrown.
				default:
					if( !Character.isWhitespace( curChar ) ) {
						throw new ParseException("Character '" + curChar + "' is not a valid start for either a variable, an expression or a value declaration.", charCount);
					}
			}
		} while( curChar != ')' );

	return new Expression( opReference, evaluables );
	}
}
