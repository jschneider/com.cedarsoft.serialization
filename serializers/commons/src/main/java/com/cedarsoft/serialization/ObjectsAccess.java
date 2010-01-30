package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @param <I> the type of the id
 * @param <E> the exception type
 */
public interface ObjectsAccess<I, E extends Exception> {
  /**
   * Returns the ids
   *
   * @return the ids
   *
   * @throws E
   */
  @NotNull
  Set<? extends I> getIds() throws E;
}
