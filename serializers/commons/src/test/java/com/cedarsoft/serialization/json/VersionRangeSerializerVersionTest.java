package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

public class VersionRangeSerializerVersionTest
  extends AbstractJsonVersionTest2<VersionRange> {

  @DataPoint
  public static final VersionEntry ENTRY1 = VersionRangeSerializerVersionTest.create(
    Version.valueOf( 1, 0, 0 ),
    VersionRangeSerializerVersionTest.class.getResource( "VersionRange_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<VersionRange> getSerializer() throws Exception {
    return new VersionRangeSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull VersionRange deserialized, @NotNull Version version ) throws Exception {
    Assert.assertEquals( "1.0.0", deserialized.getMin().format() );
    Assert.assertEquals( "1.9.17", deserialized.getMax().format() );
    Assert.assertEquals( true, deserialized.isIncludeLower() );
    Assert.assertEquals( true, deserialized.isIncludeUpper() );
  }

}
