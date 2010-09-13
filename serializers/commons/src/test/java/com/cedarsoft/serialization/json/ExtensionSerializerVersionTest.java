package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.file.Extension;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

public class ExtensionSerializerVersionTest
  extends AbstractJsonVersionTest2<Extension> {

  @DataPoint
  public static final VersionEntry ENTRY1 = ExtensionSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), ExtensionSerializerVersionTest.class.getResource( "Extension_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<Extension> getSerializer() throws Exception {
    return new ExtensionSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull Extension deserialized, @NotNull Version version )
    throws Exception {
    Assert.assertEquals( "daDelimiter", deserialized.getDelimiter() );
    Assert.assertEquals( "daExtension", deserialized.getExtension() );
  }

}
