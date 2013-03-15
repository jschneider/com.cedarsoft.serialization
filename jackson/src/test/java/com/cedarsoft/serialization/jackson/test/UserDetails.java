/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization.jackson.test;


import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserDetails {
  private long registrationDate;

  private long lastLogin;

  @Nonnull

  private byte[] passwordHash = new byte[0];

  public UserDetails() {
  }

  public UserDetails( long registrationDate, long lastLogin, @Nonnull byte[] passwordHash ) {
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

  @Nonnull
  public byte[] getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash( @Nonnull byte[] passwordHash ) {
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

  @Override
  public String toString() {
    return "UserDetails{" +
      "registrationDate=" + registrationDate +
      ", lastLogin=" + lastLogin +
      ", passwordHash=" + passwordHash +
      '}';
  }
}
