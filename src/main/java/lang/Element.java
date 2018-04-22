package duct.main.lang;


/**
  * A class to represent any functional element in the duct language.
 **/
public abstract class Element{
  public final String name;

  public Element(CharSequence name){
    this.name = name.toString();
  }

  //A horse with no name.
  public Element(){
    this.name = "";
  }
}
