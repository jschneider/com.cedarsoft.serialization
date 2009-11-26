package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Abstract class for serializer tests
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractSerializerMultiTest<T> {
  @Test
  public void testSerializer() throws IOException, SAXException {
    Serializer<T> serializer = getSerializer();

    Iterable<? extends T> objectsToSerialize = createObjectsToSerialize();

    //Serialize
    List<? extends byte[]> serialized = serialize( serializer, objectsToSerialize );

    //Verify
    verifySerialized( serialized );


    List<T> deserialized = new ArrayList<T>();
    for ( byte[] currentSerialized : serialized ) {
      deserialized.add( serializer.deserialize( new ByteArrayInputStream( currentSerialized ) ) );
    }

    verifyDeserialized( deserialized );
  }

  @NotNull
  private List<? extends byte[]> serialize( @NotNull Serializer<T> serializer, @NotNull Iterable<? extends T> objectsToSerialize ) throws IOException {
    List<byte[]> serialized = new ArrayList<byte[]>();

    int index = 0;
    for ( T objectToSerialize : objectsToSerialize ) {
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize( objectToSerialize, out );

        serialized.add( out.toByteArray() );
        index++;
      } catch ( IOException e ) {
        throw new IOException( "Serialization failed for (" + index + ") <" + objectsToSerialize + ">", e );
      }
    }
    return serialized;
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
   * @param serialized the serialized objects (within the same order)
   * @throws SAXException
   * @throws IOException
   */
  protected abstract void verifySerialized( @NotNull List<? extends byte[]> serialized ) throws SAXException, IOException;

  /**
   * Creates the object to serialize
   *
   * @return the object to serialize
   */
  @NotNull
  protected abstract Iterable<? extends T> createObjectsToSerialize();

  /**
   * Verifies the deserialized object.
   * The default implementation simply calls equals
   *
   * @param deserialized the deserialized object
   */
  protected void verifyDeserialized( @NotNull List<? extends T> deserialized ) {
    int index = 0;
    for ( T currentExpected : createObjectsToSerialize() ) {
      assertEquals( deserialized.get( index ), currentExpected );
      index++;
    }
  }
}