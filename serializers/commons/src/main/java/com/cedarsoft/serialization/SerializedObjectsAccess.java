package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.Set;

/**
 * The serialized objects access
 */
public interface SerializedObjectsAccess {
  /**
   * Returns all stored ids
   *
   * @return the stored ids
   *
   * @throws FileNotFoundException
   */
  @NotNull
  @NonNls
  Set<? extends String> getStoredIds() throws FileNotFoundException;
}