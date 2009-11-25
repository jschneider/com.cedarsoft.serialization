package com.cedarsoft.serialization;

import com.cedarsoft.file.Extension;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializerTest;
import org.jetbrains.annotations.NotNull;
import org.testng.*;

/**
 *
 */
public class ExtensionSerializerTest extends AbstractStaxMateSerializerTest<Extension> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<Extension> getSerializer() {
    return new ExtensionSerializer();
  }

  @NotNull
  @Override
  protected Extension createObjectToSerialize() {
    return new Extension( ",", "jpg" );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<extension delimiter=\",\">jpg</extension>";
  }

  @Override
  protected void verifyDeserialized( @NotNull Extension extension ) {
    Assert.assertEquals( new Extension( ",", "jpg" ), extension );
  }
}
