package com.cedarsoft.test;

/**
 *
 */
public class Model {
  private final String name;

  public Model( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Model ) ) return false;

    Model model = ( Model ) o;

    if ( name != null ? !name.equals( model.name ) : model.name != null ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
