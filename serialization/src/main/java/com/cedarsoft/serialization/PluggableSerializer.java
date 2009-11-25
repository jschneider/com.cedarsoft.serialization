package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * A pluggable serializer that can be used to stack several serializers together
 *
 * @param <T> the type
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 */
public interface PluggableSerializer<T, S, D, E extends Throwable> extends Serializer<T>{
  /**
   * Serializes the object to the given element
   *
   * @param serializeTo the serializeTo
   * @param object      the object
   * @return the serializeTo (for fluent writing)
   */
  @NotNull
  S serialize( @NotNull S serializeTo, @NotNull T object ) throws IOException, E;

  /**
   * Deserializes the object from the given document
   *
   * @param deserializeFrom the deserializeFrom
   * @return the deserialized object
   */
  @NotNull
  T deserialize( @NotNull D deserializeFrom ) throws IOException, E;
}