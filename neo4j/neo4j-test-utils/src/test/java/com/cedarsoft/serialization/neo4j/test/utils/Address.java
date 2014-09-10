package com.cedarsoft.serialization.neo4j.test.utils;

import javax.annotation.Nonnull;

/**
 * The type Address.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Address {
  @Nonnull
  private final String street;
  @Nonnull
  private final String town;

  /**
   * Instantiates a new Address.
   *
   * @param street the street
   * @param town the town
   */
  public Address( @Nonnull String street, @Nonnull String town ) {
    this.street = street;
    this.town = town;
  }

  /**
   * Gets street.
   *
   * @return the street
   */
  @Nonnull
  public String getStreet() {
    return street;
  }

  /**
   * Gets town.
   *
   * @return the town
   */
  @Nonnull
  public String getTown() {
    return town;
  }
}
