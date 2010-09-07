package com.cedarsoft.serialization.stax.test;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Role {
  private final int id;
  @NotNull
  @NonNls
  private final String description;

  public Role( int id, @NotNull String description ) {
    this.id = id;
    this.description = description;
  }

  public int getId() {
    return id;
  }

  @NotNull
  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;

    Role role = ( Role ) o;

    if ( id != role.id ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
