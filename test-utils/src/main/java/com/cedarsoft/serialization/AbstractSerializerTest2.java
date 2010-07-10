package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;
import org.junit.runner.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Abstract base class for serializer tests.
 * @param <T> the type of domain object
 */
@RunWith( Theories.class )
public abstract class AbstractSerializerTest2<T> {
  @Theory
  public void testSerializer( @NotNull Entry<T> entry ) throws Exception {
    Serializer<T> serializer = getSerializer();

    //Serialize
    byte[] serialized = serialize( serializer, entry.object );

    //Verify
    verifySerialized( entry, serialized );

    verifyDeserialized( serializer.deserialize( new ByteArrayInputStream( serialized ) ), entry.object );
  }

  @NotNull
  protected byte[] serialize( @NotNull Serializer<T> serializer, @NotNull T objectToSerialize ) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( objectToSerialize, out );
    return out.toByteArray();
  }

  protected abstract void verifySerialized( @NotNull Entry<T> entry, @NotNull byte[] serialized ) throws Exception;

  /**
   * Returns the serializer
   *
   * @return the serializer
   */
  @NotNull
  protected abstract Serializer<T> getSerializer() throws Exception;

  /**
   * Verifies the deserialized object
   *
   * @param deserialized the deserialized object
   * @param original     the original
   */
  protected void verifyDeserialized( @NotNull T deserialized, @NotNull T original ) {
    assertEquals( deserialized, original );
    assertThat( deserialized, is( new ReflectionEquals( original ) ) );
  }

  protected static <T> Entry<T> create( @NotNull T object, @NotNull @NonNls String expected ) {
    return new Entry<T>( object, expected );
  }

  public static class Entry<T> {
    @NotNull
    private final T object;
    @NotNull
    @NonNls
    private final String expected;

    public Entry( @NotNull T object, @NotNull @NonNls String expected ) {
      this.object = object;
      this.expected = expected;
    }

    @NotNull
    public String getExpected() {
      return expected;
    }

    @NotNull
    public T getObject() {
      return object;
    }
  }
}
