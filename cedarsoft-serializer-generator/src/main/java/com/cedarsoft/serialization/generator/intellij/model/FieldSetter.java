package com.cedarsoft.serialization.generator.intellij.model;

import javax.annotation.Nonnull;

/**
 * Describes a setter for a field
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface FieldSetter {
  /**
   * Whether it is constructed by access
   *
   * @return the constructor access
   */
  boolean isConstructorAccess();

  /**
   * Whether it is accessed by setter
   *
   * @return the setter
   */
  boolean isSetterAccess();

  /**
   * Implementation that uses a constructor
   *
   * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
   */
  class ConstructorFieldSetter implements FieldSetter {
    private final int parameterIndex;

    public ConstructorFieldSetter( int parameterIndex ) {
      this.parameterIndex = parameterIndex;
    }

    public int getParameterIndex() {
      return parameterIndex;
    }

    @Override
    public boolean isConstructorAccess() {
      return true;
    }

    @Override
    public boolean isSetterAccess() {
      return false;
    }
  }

  /**
   * Implementation that uses a setter
   *
   * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
   */
  class SetterFieldSetter implements FieldSetter {
    @Nonnull
    private final String setter;

    public SetterFieldSetter( @Nonnull String setter ) {
      this.setter = setter;
    }

    @Nonnull
    public String getSetter() {
      return setter;
    }

    @Override
    public boolean isConstructorAccess() {
      return false;
    }

    @Override
    public boolean isSetterAccess() {
      return true;
    }
  }
}
