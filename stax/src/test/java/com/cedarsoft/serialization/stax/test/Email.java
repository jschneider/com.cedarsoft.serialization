package com.cedarsoft.serialization.stax.test;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Email {
  @NotNull @NonNls
  private final String mail;

  public Email( @NotNull String mail ) {
    this.mail = mail;
  }

  @NotNull
  public String getMail() {
    return mail;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;

    Email email = ( Email ) o;

    if ( !mail.equals( email.mail ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return mail.hashCode();
  }
}
