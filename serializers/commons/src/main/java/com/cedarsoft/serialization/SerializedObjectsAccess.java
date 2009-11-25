package com.cedarsoft.serialization;

import com.cedarsoft.StillContainedException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * The serialized objects access
 */
public interface SerializedObjectsAccess {
  /**
   * Returns the output for the given id
   *
   * @param id the id
   * @return the output stream
   *
   * @throws FileNotFoundException
   * @throws StillContainedException if an object with the given id is still contained
   */
  @NotNull
  OutputStream openOut( @NotNull @NonNls String id ) throws StillContainedException, FileNotFoundException;

  /**
   * Returns all stored ids
   *
   * @return the stored ids
   */
  @NotNull
  @NonNls
  Set<? extends String> getStoredIds() throws FileNotFoundException;

  /**
   * Returns the input stream
   *
   * @param id the id
   * @return the input stream
   *
   * @throws FileNotFoundException
   */
  @NotNull
  InputStream getInputStream( @NotNull @NonNls String id ) throws FileNotFoundException;
}
