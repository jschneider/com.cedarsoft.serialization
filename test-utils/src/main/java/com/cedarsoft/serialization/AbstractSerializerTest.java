package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Abstract class for serializer tests
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractSerializerTest<T> {
  @Test
  public void testSerializer() throws IOException, SAXException {
    Serializer<T> serializer = getSerializer();

    T objectToSerialize = createObjectToSerialize();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( objectToSerialize, out );

    byte[] serialized = out.toByteArray();
    verifySerialized( serialized );

    T deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );

    verifyDeserialized( deserialized );
  }

  /**
   * Returns the serializer
   *
   * @return the serializer
   */
  @NotNull
  protected abstract Serializer<T> getSerializer();

  /**
   * Verifies the serialized object
   *
   * @param serialized the serialized object
   * @throws SAXException
   * @throws IOException
   */
  protected abstract void verifySerialized( @NotNull byte[] serialized ) throws SAXException, IOException;

  /**
   * Creates the object to serialize
   *
   * @return the object to serialize
   */
  @NotNull
  protected abstract T createObjectToSerialize();

  /**
   * Verifies the deserialized object.
   * The default implementation simply calls equals
   *
   * @param deserialized the deserialized object
   */
  protected void verifyDeserialized( @NotNull T deserialized ) {
    assertEquals( deserialized, createObjectToSerialize() );
  }
}
