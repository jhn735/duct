package duct.main.lang;

/* Each class that is evaluable has the responsibility of defining a way
 * for it to evaluate itself. Context at time of evaluation can only be guaranteed
 * if the implementing class requires it..
 */
public interface Evaluable{
  public Value evaluate();
}
