package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class WindowSerializerTest extends AbstractXmlSerializerTest<Window> {
  @NotNull
  @Override
  protected Serializer<Window> getSerializer() throws Exception {
    return new Window.Serializer();
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<window width=\"123.3\" height=\"444.4\">\n" +
      "  <description>the window</description>\n" +
      "</window>";
  }

  @NotNull
  @Override
  protected Window createObjectToSerialize() throws Exception {
    return new Window( "the window", 123.3, 444.4 );
  }
}
