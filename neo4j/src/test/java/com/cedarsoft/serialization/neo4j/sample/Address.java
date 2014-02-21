package com.cedarsoft.serialization.neo4j.sample;

import javax.annotation.Nonnull;

/**
* @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
*/
public class Address {
  @Nonnull
  private final String street;
  @Nonnull
  private final String town;

  public Address( @Nonnull String street, @Nonnull String town ) {
    this.street = street;
    this.town = town;
  }

  @Nonnull
  public String getStreet() {
    return street;
  }

  @Nonnull
  public String getTown() {
    return town;
  }
}
