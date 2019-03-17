package duct.lang;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.lang.Character;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;
import java.text.ParseException;
import java.util.stream.Collectors;

import duct.lang.value.Value;

/**
 * Represents a statement in the duct language which does something. That 
 * includes, creating values, calling values, executing scripts... Creating a 
 * class rather than a function responsible for parsing expressions allows one 
 * to run an expression without parsing the script again.
**/
//expressions must have one operation and a set of zero or more values on which the operation works on. 
public class Expression extends Element implements Evaluable {
	private static final String UNINITIALIZED_EXECUTOR_ERR_MSG =
			"Executor for expression must be initializied before getting the operation.";

	private static final String EXPRESSION_NOT_ENCLOSED_ERR_MSG =
			"An expression must be enclosed between '(' and ')'.";

	private Operation       _operation;
	private String          _operationReference;
	private List<Evaluable> _evaluables;
	private Executor        _executor;

	public Expression( Operation op, List<Evaluable> evaluables ){
		super();
		this._operationReference = op.getName();
		this._operation  = op;
		this._evaluables = evaluables;
	}

	public Expression( String opReference, List<Evaluable> evaluables ){
		this( opReference, evaluables, null );
	}

	public Expression( String opReference, List<Evaluable> evaluables, Executor exe ){
		super();
		this._operationReference = opReference;
		this._evaluables = Collections.unmodifiableList( evaluables );
		this._executor   = exe;

		if( this._executor != null ){
			this._operation = this._executor.getOperation( this._operationReference );
		}
	}

	public List<Evaluable> getEvaluables(){
		return this._evaluables;
	}

	public Executor getExecutor(){
		return this._executor;
	}

	public boolean hasExecutor(){
		return this._executor != null;
	}

	public Operation getOperation(){
		if( !this.hasExecutor() ){
			throw new IllegalStateException( Expression.UNINITIALIZED_EXECUTOR_ERR_MSG );
		}

		if( this._operation == null ){
			this._operation = this.getExecutor().getOperation( this._operationReference );
		}

		return this._operation;
	}

	//create a list of values from the list of evaluables given in the constructor
	public List<Value> getValues(){
		return this._evaluables.stream()
				.map( Evaluable::evaluate )
				.collect( Collectors.toList() );
	}

	public Value evaluate(){
		if( this._executor == null ){
			throw new IllegalStateException( "Executor must be set in order to evaluate an expression." );
		}

		return this.evaluate( this._executor );
	}

	public Value evaluate( Executor exe ){
		return _operation.execute( exe, this.getValues() );
	}

	public static Expression nextExpression( Reader reader ) throws ParseException, IOException {
		int charCount = 0;
		char curChar = ParseUtils.readNextChar( reader );
		PushbackReader pReader = new PushbackReader( reader );

		String opReference;

		List<Evaluable> evaluables = new ArrayList<>();

		//throw a fit of the expression is not started properly
		if( curChar != '(' ){
			throw new ParseException( Expression.EXPRESSION_NOT_ENCLOSED_ERR_MSG, charCount );
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
					if( !Character.isWhitespace( curChar ) ){
						throw new ParseException(
								"Character '" + curChar + "' is not a valid start for either a variable, an expression or a value declaration.",
								charCount
						);
					}
			}
		} while( curChar != ')' );

		return new Expression( opReference, evaluables );
	}
}
