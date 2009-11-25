package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes objects
 *
 * @param <T> the type of the objects
 */
public interface Serializer<T> {
  /**
   * The target of the processing instruction containing the version information
   */
  @NotNull
  @NonNls
  String PI_TARGET_FORMAT = "format";

  /**
   * Serializes the object to the given output stream
   *
   * @param object the object
   * @param out    the out stream
   */
  void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException;

  /**
   * Deserializes the object from the input stream
   *
   * @param in the input stream
   * @return the deserialized object
   */
  @NotNull
  T deserialize( @NotNull InputStream in ) throws IOException;

  /**
   * Returns the format version that is written
   *
   * @return the format version that is written
   */
  @NotNull
  Version getFormatVersion();
}