package com.cedarsoft.serialization.generator.model;

import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class DefaultFieldTypeInformation implements FieldTypeInformation {
  @NotNull
  private final TypeMirror typeMirror;

  public DefaultFieldTypeInformation( @NotNull TypeMirror typeMirror ) {
    this.typeMirror = typeMirror;
  }

  @NotNull
  @Override
  public TypeMirror getType() {
    return typeMirror;
  }

  @Override
  public boolean isType( @NotNull Class<?> type ) {
    return getType().toString().equals( type.getName() );
  }
}
