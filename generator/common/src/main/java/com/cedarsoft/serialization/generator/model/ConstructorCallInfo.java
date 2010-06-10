package com.cedarsoft.serialization.generator.model;

import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import org.jetbrains.annotations.NotNull;

/**
*
*/
public class ConstructorCallInfo {
  @NotNull
  private final ConstructorDeclaration constructorDeclaration;
  private final int index;
  @NotNull
  private final ParameterDeclaration parameterDeclaration;

  public ConstructorCallInfo( @NotNull ConstructorDeclaration constructorDeclaration, int index, @NotNull ParameterDeclaration parameterDeclaration ) {
    this.constructorDeclaration = constructorDeclaration;
    this.index = index;
    this.parameterDeclaration = parameterDeclaration;
  }

  @NotNull
  public ConstructorDeclaration getConstructorDeclaration() {
    return constructorDeclaration;
  }

  public int getIndex() {
    return index;
  }

  @NotNull
  public ParameterDeclaration getParameterDeclaration() {
    return parameterDeclaration;
  }
}
