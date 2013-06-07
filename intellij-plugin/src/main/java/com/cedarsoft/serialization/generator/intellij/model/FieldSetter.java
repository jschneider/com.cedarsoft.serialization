package com.cedarsoft.serialization.generator.intellij.model;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface FieldSetter {
  boolean isConstructorAccess();

  boolean isSetterAccess();

  /**
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
