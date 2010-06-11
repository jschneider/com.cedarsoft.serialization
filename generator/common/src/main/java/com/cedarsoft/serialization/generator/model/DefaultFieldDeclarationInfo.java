package com.cedarsoft.serialization.generator.model;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class DefaultFieldDeclarationInfo extends DefaultFieldTypeInformation implements FieldDeclarationInfo {
  @NotNull
  protected final FieldDeclaration fieldDeclaration;
  @NotNull
  protected final MethodDeclaration getterDeclaration;

  public DefaultFieldDeclarationInfo( @NotNull MethodDeclaration getterDeclaration, @NotNull FieldDeclaration fieldDeclaration ) {
    super( fieldDeclaration.getType() );
    this.getterDeclaration = getterDeclaration;
    this.fieldDeclaration = fieldDeclaration;
  }

  @Override
  @NotNull
  public FieldDeclaration getFieldDeclaration() {
    return fieldDeclaration;
  }

  @NotNull
  @Override
  public MethodDeclaration getGetterDeclaration() {
    return getterDeclaration;
  }

  @NotNull
  @Override
  @NonNls
  public String getSimpleName() {
    return fieldDeclaration.getSimpleName();
  }
}
