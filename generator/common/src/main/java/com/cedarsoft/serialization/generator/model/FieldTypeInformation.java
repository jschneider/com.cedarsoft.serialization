package com.cedarsoft.serialization.generator.model;

import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NotNull;

/**
 * Informations about a field type
 */
public interface FieldTypeInformation {
  /**
   * Returns the type of the field
   *
   * @return the type of the field
   */
  @NotNull
  TypeMirror getType();

  /**
   * Whether the field is of the given type (exactly!)
   *
   * @param type the type
   * @return true if the field is of the given type
   */
  boolean isType( @NotNull Class<?> type );
}