package com.cedarsoft.sample.fixed;

/**
 * Represents a money object
 */
//START SNIPPET: main
public class Money {
  private final long cents;

  public Money( long cents ) {
    this.cents = cents;
  }

  @Deprecated
  public Money( double amount ) {
    this( convertValueToCents( amount ) );
  }

  @Deprecated
  public double getAmount() {
    return cents / 100.0;
  }

  public long getCents() {
    return cents;
  }

  public static long convertValueToCents( double amount ) {
    return Math.round( amount * 100 );
  }
  //END SNIPPET: main

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
    return ( int ) ( cents ^ ( cents >>> 32 ) );
  }
}
