package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.app.Application;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

public class ApplicationSerializerVersionTest extends AbstractJsonVersionTest2<Application> {
  @DataPoint
  public static final VersionEntry ENTRY1 = ApplicationSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), ApplicationSerializerVersionTest.class.getResource( "Application_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<Application> getSerializer() throws Exception {
    return new ApplicationSerializer( new VersionSerializer() );
  }

  @Override
  protected void verifyDeserialized( @NotNull Application deserialized, @NotNull Version version ) throws Exception {
    Assert.assertEquals( "name", deserialized.getName() );
    Assert.assertEquals( "1.2.3", deserialized.getVersion().toString() );
  }

}
