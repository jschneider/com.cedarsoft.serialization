package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Converts objects to strings
 *
 * @param <T> the string
 */
public interface ToString<T> {
  /**
   * Returns the string representation
   *
   * @param object the object
   * @return the string representation
   */
  @NotNull
  @NonNls
  String convert( @NotNull T object );
}
