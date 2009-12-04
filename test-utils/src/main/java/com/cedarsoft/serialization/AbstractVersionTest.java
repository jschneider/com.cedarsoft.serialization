package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Abstract test class for testing the support for multiple format versions
 *
 * @param <T> the type that is deserialized
 */
public abstract class AbstractVersionTest<T> {
  /**
   * This method checks old serialized objects
   *
   * @throws IOException
   * @throws SAXException
   */
  @Test
  public void testVersions() throws IOException, SAXException {
    Serializer<T> serializer = getSerializer();

    Map<? extends Version, ? extends byte[]> serializedMap = getSerialized();

    for ( Map.Entry<? extends Version, ? extends byte[]> entry : serializedMap.entrySet() ) {
      Version version = entry.getKey();
      byte[] serialized = entry.getValue();

      T deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );

      verifyDeserialized( deserialized, version );
    }
  }

  /**
   * Returns the serializer
   *
   * @return the serializer
   */
  @NotNull
  protected abstract Serializer<T> getSerializer();

  /**
   * Returns a map containing the version and the serialized object
   *
   * @return a map containing the version and the serialized object
   */
  @NotNull
  protected abstract Map<? extends Version, ? extends byte[]> getSerialized();

  /**
   * Verifies the deserialized object.
   *
   * @param deserialized the deserialized object
   * @param version      the version
   */
  protected abstract void verifyDeserialized( @NotNull T deserialized, @NotNull Version version );
}
