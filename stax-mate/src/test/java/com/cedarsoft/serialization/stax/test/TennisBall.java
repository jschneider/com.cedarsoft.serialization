package com.cedarsoft.serialization.stax.test;

/**
*
*/
public class TennisBall implements Ball {
  private final int id;

  public TennisBall( int id ) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof TennisBall ) ) return false;

    TennisBall that = ( TennisBall ) o;

    if ( id != that.id ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
