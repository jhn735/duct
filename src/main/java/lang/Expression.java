package duct.main.lang;
import java.util.List;
import java.util.ArrayList;
import java.lang.CharSequence;
import java.lang.Character;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;
import java.text.ParseException;
import duct.main.lang.ParseUtils;

/**
 * Represents a statement in the duct language which does something. That 
 * includes, creating values, calling values, executing scripts... Creating a 
 * class rather than a function responsible for parsing expressions allows one 
 * to run an expression without parsing the script again.
**/
//expressions must have one operation and a set of zero or more values on which the operation works on. 
public class Expression extends Element implements Evaluable {
	public final Operation operation;
	private List<Evaluable> evaluables;

	public List<Evaluable> evaluables(){
		return new ArrayList<Evaluable>(evaluables);
	}

	//create a list of values from the list evaluables given in the constructor
	public List<Value> values() {
		List<Value> values = new ArrayList<Value>();
		for(Evaluable e:evaluables)
			values.add(e.evaluate());
	return values;
	}

	public Expression( Operation op, List<Evaluable> evaluables ) {
		super();
		this.operation = op;
		this.evaluables = evaluables;
	} 

	public Value evaluate() {
		return operation.execute(this.values());
	}

	private static final String EXPRESSION_NOT_ENCLOSED_MSG="An expression must be enclosed between '(' and ')'.";

	public static Expression nextExpression( Reader reader, Executor exe )
		throws ParseException, IOException {
		int charCount = 0;
		char curChar = ParseUtils.readNextChar(reader);
		PushbackReader pReader = new PushbackReader(reader);

		Operation op = null;
		List<Evaluable> evaluables = new ArrayList<Evaluable>();

		//throw a fit of the expression is not started properly
		if(curChar != '(')
			throw new ParseException(EXPRESSION_NOT_ENCLOSED_MSG, charCount);

		//assuming the basic stuff is out the way, get the name of the operation and then
		//get the operation.
		StringBuilder name = new StringBuilder();
		while(!Character.isWhitespace(curChar)){
			curChar = ParseUtils.readNextChar(pReader);
			charCount++;
			name.append(curChar);
		}

		op = exe.getOperation(name);

		do{
			curChar = ParseUtils.readNextChar(pReader);
			charCount++;

			switch(curChar){
				case '(':
					pReader.unread(curChar);
					evaluables.add(Expression.nextExpression(pReader, exe));
				break;

				//if the character is a '<' get the value it's suppose to represent
				case '<':
					pReader.unread(curChar);
					evaluables.add(Value.nextValue(pReader));
				break;

				case '$':
					name.setLength(0);
					while(curChar != ' '){
						curChar = ParseUtils.readNextChar(pReader);
						charCount++;
						name.append(curChar);
					}
					evaluables.add(exe.getValue(name));
				break;
				//Anything else should cause an error to be thrown.
				default:
					if(!Character.isWhitespace(curChar))
						throw new ParseException("Character '" + curChar + "' is not a valid start for either a variable, an expression or a value declaration.", charCount);
			}
		} while(curChar != ')');

	return new Expression(op, evaluables);
	}
}
