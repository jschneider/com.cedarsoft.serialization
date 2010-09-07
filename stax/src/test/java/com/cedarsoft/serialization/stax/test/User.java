package com.cedarsoft.serialization.stax.test;

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

  public User( @NotNull String name, @NotNull Collection<? extends Email> emails, Collection<? extends Role> roles ) {
    this.name = name;
    this.emails.addAll( emails );
    this.roles.addAll( roles );
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
}
