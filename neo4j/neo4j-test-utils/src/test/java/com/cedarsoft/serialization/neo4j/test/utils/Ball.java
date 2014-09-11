package com.cedarsoft.serialization.neo4j.test.utils;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface Ball {
  class TennisBall implements Ball {
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

  class BasketBall implements Ball {
    @Nonnull
    private final String theId;

    public BasketBall( @Nonnull String theId ) {
      this.theId = theId;
    }

    @Nonnull
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
}