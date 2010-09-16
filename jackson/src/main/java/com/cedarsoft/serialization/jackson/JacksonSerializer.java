package com.cedarsoft.serialization.jackson;

import com.cedarsoft.serialization.PluggableSerializer;

/**
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to (e.g. a dom element or stream)
 * @param <D> the object to deserialize from ((e.g. a dom element or stream)
 * @param <E> the exception that might be thrown
 */
public interface JacksonSerializer<T, S, D, E extends Throwable> extends PluggableSerializer<T, S, D, E> {
  boolean isObjectType();
}
