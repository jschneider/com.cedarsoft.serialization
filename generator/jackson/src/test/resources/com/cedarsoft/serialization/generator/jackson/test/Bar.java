package com.cedarsoft.serialization.generator.jackson.test;

/**
 *
 */
public class Bar {
  private final int id;

  public Bar( int id ) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void getIt() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Bar ) ) return false;

    Bar bar = ( Bar ) o;

    if ( id != bar.id ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return "Bar{" +
      "id=" + id +
      '}';
  }
}
