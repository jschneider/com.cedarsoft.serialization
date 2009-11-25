package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Abstract base class for XML based serializers.
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractXmlSerializerTest<T> extends AbstractSerializerTest<T> {
  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws SAXException, IOException {
    AssertUtils.assertXMLEqual( new String( serialized ), getExpectedSerialized() );
  }

  /**
   * Returns the expected serialized string
   *
   * @return the expected serialized string
   */
  @NotNull
  @NonNls
  protected abstract String getExpectedSerialized();
}
