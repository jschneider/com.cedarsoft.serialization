package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializer with several convenience methods.
 *
 * @param <T> the type
 */
@Deprecated
public interface ExtendedSerializer<T> extends Serializer<T> {
  /**
   * Serializes the given object
   *
   * @param object  the object
   * @return the serialized object
   *
   * @throws IOException
   */
  @Deprecated
  @NotNull
  byte[] serialize( @NotNull T object ) throws IOException;

  /**
   * Serializes the object to the output stream
   *
   * @param object the object
   * @param out    the out stream
   * @throws IOException
   */
  @Deprecated
  void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException;

  /**
   * Deserializes the object
   *
   * @param in the input stream
   * @return the deserialized object
   *
   * @throws IOException
   */
  @Deprecated
  @NotNull
  T deserialize( @NotNull InputStream in ) throws IOException;
}