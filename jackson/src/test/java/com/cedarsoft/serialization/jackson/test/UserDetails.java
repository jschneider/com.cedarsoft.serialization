package com.cedarsoft.serialization.jackson.test;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserDetails {
  private long registrationDate;

  private long lastLogin;

  @NotNull
  @NonNls
  private byte[] passwordHash;

  public UserDetails() {
  }

  public UserDetails( long registrationDate, long lastLogin, @NotNull byte[] passwordHash ) {
    this.registrationDate = registrationDate;
    this.lastLogin = lastLogin;
    this.passwordHash = passwordHash;
  }

  public long getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate( long registrationDate ) {
    this.registrationDate = registrationDate;
  }

  public long getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin( long lastLogin ) {
    this.lastLogin = lastLogin;
  }

  @NotNull
  public byte[] getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash( @NotNull byte[] passwordHash ) {
    this.passwordHash = passwordHash;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;

    UserDetails that = ( UserDetails ) o;

    if ( lastLogin != that.lastLogin ) return false;
    if ( registrationDate != that.registrationDate ) return false;
    if ( !Arrays.equals( passwordHash, that.passwordHash ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = ( int ) ( registrationDate ^ ( registrationDate >>> 32 ) );
    result = 31 * result + ( int ) ( lastLogin ^ ( lastLogin >>> 32 ) );
    result = 31 * result + Arrays.hashCode( passwordHash );
    return result;
  }
}
