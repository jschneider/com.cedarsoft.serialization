package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for all types of serializers.<br/>
 * <p/>
 * Each serializer is able to serialize an object to a given output stream.
 * <p/>
 * A format version is supported for each serializer.
 *
 * @param <T> the type of the objects this serializer is able to (de)serialize
 */
public interface Serializer<T> {
  /**
   * Serializes the object to the given output stream
   *
   * @param object the object to serialize
   * @param out    the out stream
   * @throws IOException
   */
  void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException;

  /**
   * Deserializes the object from the input stream
   *
   * @param in the input stream
   * @return the deserialized object
   *
   * @throws VersionException if any version related problem occurred
   * @throws IOException
   */
  @NotNull
  T deserialize( @NotNull InputStream in ) throws IOException, VersionException;

  /**
   * Returns the format version that is written.
   *
   * @return the format version that is written
   */
  @NotNull
  Version getFormatVersion();

  @NotNull
  VersionRange getFormatVersionRange();
}