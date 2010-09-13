package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.junit.*;
import org.junit.experimental.theories.*;

public class VersionSerializerVersionTest
  extends AbstractJsonVersionTest2<Version> {

  @DataPoint
  public static final VersionEntry ENTRY1 = VersionSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), VersionSerializerVersionTest.class.getResource( "Version_1.0.0_1.json" ) );

  @Override
  protected Serializer<Version> getSerializer()
    throws Exception {
    return new VersionSerializer();
  }

  @Override
  protected void verifyDeserialized( Version deserialized, Version version )
    throws Exception {
    Assert.assertEquals( 42, deserialized.getMajor() );
    Assert.assertEquals( 42, deserialized.getMinor() );
    Assert.assertEquals( 42, deserialized.getBuild() );
    Assert.assertEquals( "suffix", deserialized.getSuffix() );
  }

}
