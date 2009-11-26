package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a strategy to serialize an object.
 * <p/>
 * Those strategies might be used to serialize different objects of the same type.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the serializing object
 * @param <D> the deserializing object
 * @param <E> the exception that might be thrown
 */
public interface SerializingStrategy<T, S, D, E extends Throwable> extends PluggableSerializer<T, S, D, E> {
  /**
   * Returns the id that is used to identify the strategy/object
   *
   * @return the id
   */
  @NotNull
  @NonNls
  String getId();

  /**
   * Whether the given reference type is supported.
   * This method is called to identify the strategy that is used to serialize the object.
   *
   * @param object the reference
   * @return true if this strategy supports the reference, false otherwise
   */
  boolean supports( @NotNull Object object );
}
