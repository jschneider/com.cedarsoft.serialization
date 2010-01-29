package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 */
public interface RegistrySerializingStrategy<T> {
  @NotNull
  T deserialize( @NotNull @NonNls String id, @NotNull SerializedObjectsAccess serializedObjectsAccess ) throws IOException;

  void serialize( @NotNull T object, @NotNull @NonNls String id, @NotNull SerializedObjectsAccess serializedObjectsAccess ) throws IOException;
}
