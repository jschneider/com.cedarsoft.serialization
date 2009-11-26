package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * This is a special serializer that is able to serialize an object to a given element.
 * The pluggable serializers can be used to delegate the serialization for child objects.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to (e.g. a dom element or stream)
 * @param <D> the object to deserialize from ((e.g. a dom element or stream)
 * @param <E> the exception that might be thrown
 */
public interface PluggableSerializer<T, S, D, E extends Throwable> extends Serializer<T> {
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