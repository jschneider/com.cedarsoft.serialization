package com.cedarsoft.serialization.neo4j.test.utils;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The type Person.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Person {
  @Nonnull
  private final String name;
  @Nonnull
  private final Address address;
  @Nonnull
  private final List<? extends Email> mails;

  /**
   * Instantiates a new Person.
   *
   * @param name the name
   * @param address the address
   * @param mails the mails
   */
  public Person( @Nonnull String name, @Nonnull Address address, @Nonnull List<? extends Email> mails ) {
    this.name = name;
    this.address = address;
    this.mails = mails;
  }

  /**
   * Gets mails.
   *
   * @return the mails
   */
  @Nonnull
  public List<? extends Email> getMails() {
    return mails;
  }

  /**
   * Gets address.
   *
   * @return the address
   */
  @Nonnull
  public Address getAddress() {
    return address;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  @Nonnull
  public String getName() {
    return name;
  }
}
