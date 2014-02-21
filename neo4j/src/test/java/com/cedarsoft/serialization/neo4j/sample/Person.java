package com.cedarsoft.serialization.neo4j.sample;

import javax.annotation.Nonnull;
import java.util.List;

/**
* @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
*/
public class Person {
  @Nonnull
  private final String name;
  @Nonnull
  private final Address address;
  @Nonnull
  private final List<? extends Email> mails;

  public Person( @Nonnull String name, @Nonnull Address address, @Nonnull List<? extends Email> mails ) {
    this.name = name;
    this.address = address;
    this.mails = mails;
  }

  @Nonnull
  public List<? extends Email> getMails() {
    return mails;
  }

  @Nonnull
  public Address getAddress() {
    return address;
  }

  @Nonnull
  public String getName() {
    return name;
  }
}
