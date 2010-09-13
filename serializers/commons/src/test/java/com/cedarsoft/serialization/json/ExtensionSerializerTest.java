package com.cedarsoft.serialization.json;

import com.cedarsoft.file.Extension;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

public class ExtensionSerializerTest extends AbstractJsonSerializerTest2<Extension> {
  @DataPoint
  public static final Entry<? extends Extension> ENTRY1 = ExtensionSerializerTest.create(
    new Extension( "daDelimiter", "daExtension" ),
    ExtensionSerializerTest.class.getResource( "Extension_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<Extension> getSerializer() throws Exception {
    return new ExtensionSerializer();
  }

}
