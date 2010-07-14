package com.cedarsoft.serialization.stax.test;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class BasketBall implements Ball {
  @NotNull
  private final String theId;

  public BasketBall( @NotNull String theId ) {
    this.theId = theId;
  }

  @NotNull
  public String getTheId() {
    return theId;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof BasketBall ) ) return false;

    BasketBall that = ( BasketBall ) o;

    if ( !theId.equals( that.theId ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return theId.hashCode();
  }
}
