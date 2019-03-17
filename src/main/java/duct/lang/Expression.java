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
public class Expression extends Element implements Evaluable {
	private static final String EXECUTOR_NEEDED_TO_EVALUATE_ERR_MSG =
			"An executor must be provided in order to evaluate an expression";

	private static final String EXPRESSION_NOT_ENCLOSED_ERR_MSG =
			"An expression must be enclosed between '(' and ')'.";

	private final String          _operationReference;
	private final List<Evaluable> _evaluables;

	public Expression( String opReference, List<Evaluable> evaluables ){
		super();
		this._operationReference = opReference;
		this._evaluables = Collections.unmodifiableList( evaluables );
	}

	public String getOperationReference(){
		return this._operationReference;
	}

	public List<Evaluable> getEvaluables(){
		return this._evaluables;
	}

	//create a list of values from the list of evaluables given in the constructor
	public List<Value> getValues(){
		return this._evaluables.stream()
				.map( Evaluable::evaluate )
				.collect( Collectors.toList() );
	}

	public Value evaluate(){
		throw new UnsupportedOperationException( Expression.EXECUTOR_NEEDED_TO_EVALUATE_ERR_MSG );
	}

	public Value evaluate( Executor exe ){
		Operation op = exe.getOperation( this._operationReference );
		return op.execute( exe, this.getValues() );
	}

	public static Expression nextExpression( Reader reader ) throws ParseException, IOException {
		Integer charCount = 0;
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
		while( !Character.isWhitespace( curChar ) && curChar != ')' ){
			curChar = ParseUtils.readNextChar( pReader, charCount );
			name.append( curChar );
		}

		opReference = name.toString();

	 while( curChar != ')' ){
			curChar = ParseUtils.readNextChar( pReader, charCount );

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
		}
		return new Expression( opReference, evaluables );
	}
}
