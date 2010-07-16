package com.cedarsoft.sample;

/**
 * Represents a money object
 */
//START SNIPPET: main
public class Money {
  private final double amount;

  public Money( double amount ) {
    this.amount = amount;
  }

  public double getAmount() {
    return amount;
  }

  //END SNIPPET: main

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Money ) ) return false;

    Money money = ( Money ) o;

    if ( Double.compare( money.amount, amount ) != 0 ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    long temp = amount != +0.0d ? Double.doubleToLongBits( amount ) : 0L;
    return ( int ) ( temp ^ ( temp >>> 32 ) );
  }
}
