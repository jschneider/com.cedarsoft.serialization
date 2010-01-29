package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A serializing strategy based on a serializer.
 * This is the default strategy and used for most {@link RegistrySerializer}s.
 *
 * @param <T> the type
 */
public class SerializerBasedRegistrySerializingStrategy<T> implements RegistrySerializingStrategy<T> {
  @NotNull
  private final Serializer<T> serializer;

  public SerializerBasedRegistrySerializingStrategy( @NotNull Serializer<T> serializer ) {
    this.serializer = serializer;
  }

  @NotNull
  @Override
  public T deserialize( @NotNull @NonNls String id, @NotNull SerializedObjectsAccess serializedObjectsAccess ) throws IOException {
    return serializer.deserialize( ( ( StreamBasedSerializedObjectsAccess ) serializedObjectsAccess ).getInputStream( id ) );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull @NonNls String id, @NotNull SerializedObjectsAccess serializedObjectsAccess ) throws IOException {
    OutputStream out = ( ( StreamBasedSerializedObjectsAccess ) serializedObjectsAccess ).openOut( id );
    try {
      serializer.serialize( object, out );
    } finally {
      out.close();
    }
  }

  @NotNull
  public Serializer<T> getSerializer() {
    return serializer;
  }
}
