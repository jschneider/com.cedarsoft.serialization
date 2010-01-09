package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class DoorSerializerTest extends AbstractXmlSerializerTest<Door> {
  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<door>" +
      "  <description>descr</description>" +
      "</door>";
  }

  @NotNull
  @Override
  protected Serializer<Door> getSerializer() throws Exception {
    return new Door.Serializer();
  }

  @NotNull
  @Override
  protected Door createObjectToSerialize() throws Exception {
    return new Door( "descr" );
  }
}
