package com.cedarsoft.serialization.generator.model;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface FieldInfo {
  /**
   * Returns the simple name of the field
   *
   * @return the simple name of the field
   */
  @NotNull
  @NonNls
  String getSimpleName();
}
