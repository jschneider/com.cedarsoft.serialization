package com.cedarsoft.test;

/**
 *
 */
public class Money {
  private int cents;

  public Money( int dollars, int cents ) {
    this.cents = dollars * 100 + cents;
  }

  public Money( int cents ) {
    this.cents = cents;
  }

  public int getCents() {
    return cents;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Money ) ) return false;

    Money money = ( Money ) o;

    if ( cents != money.cents ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return cents;
  }
}
