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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class User {
  @NotNull
  @NonNls
  private final String name;
  @NotNull
  private final List<Role> roles = new ArrayList<Role>();
  @NotNull
  private final List<Email> emails = new ArrayList<Email>();
  @NotNull
  private final UserDetails userDetails;

  public User( @NotNull String name, @NotNull Collection<? extends Email> emails, Collection<? extends Role> roles ) {
    this( name, emails, roles, new UserDetails() );
  }

  public User(  @NotNull String name, @NotNull Collection<? extends Email> emails, Collection<? extends Role> roles , @NotNull UserDetails userDetails ) {
    this.name = name;
    this.userDetails = userDetails;
    this.emails.addAll( emails );
    this.roles.addAll( roles );
  }

  @NotNull
  public UserDetails getUserDetails() {
    return userDetails;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public List<? extends Role> getRoles() {
    return Collections.unmodifiableList( roles );
  }

  @NotNull
  public List<? extends Email> getEmails() {
    return Collections.unmodifiableList( emails );
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;

    User user = ( User ) o;

    if ( !emails.equals( user.emails ) ) return false;
    if ( !name.equals( user.name ) ) return false;
    if ( !roles.equals( user.roles ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + roles.hashCode();
    result = 31 * result + emails.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "User{" +
      "name='" + name + '\'' +
      ", roles=" + roles +
      ", emails=" + emails +
      ", userDetails=" + userDetails +
      '}';
  }
}
