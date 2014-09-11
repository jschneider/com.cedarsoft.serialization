package com.cedarsoft.serialization.neo4j.test.utils;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;

/**
 * The type Email.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Email {
  @Nonnull
  private final String mail;

  /**
   * Instantiates a new Email.
   *
   * @param mail the mail
   */
  public Email( @Nonnull String mail ) {
    this.mail = mail;
  }

  /**
   * Gets mail.
   *
   * @return the mail
   */
  @Nonnull
  public String getMail() {
    return mail;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null || getClass() != obj.getClass() ) {
      return false;
    }

    Email that = ( Email ) obj;

    return Objects.equal( this.mail, that.mail );
  }

  @Override
  public int hashCode() {
    return Objects.hashCode( mail );
  }

  @Override
  public String toString() {
    return Objects.toStringHelper( this )
      .addValue( mail )
      .toString();
  }
}
