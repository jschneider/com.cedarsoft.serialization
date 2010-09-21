package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.license.License;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

public class LicenseSerializerVersionTest extends AbstractJsonVersionTest2<License> {
  @DataPoint
  public static final VersionEntry ENTRY1 = LicenseSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), LicenseSerializerVersionTest.class.getResource( "License_1.0.0_1.json" ) );

  @Override
  protected Serializer<License> getSerializer() throws Exception {
    return new LicenseSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull License deserialized, @NotNull Version version ) throws Exception {
    Assert.assertSame( License.GPL_3, deserialized );
  }
}
