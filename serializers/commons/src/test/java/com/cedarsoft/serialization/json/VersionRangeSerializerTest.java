package com.cedarsoft.serialization.json;

import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.junit.experimental.theories.*;

public class VersionRangeSerializerTest
  extends AbstractJsonSerializerTest2<VersionRange> {

  @DataPoint
  public static final Entry<? extends VersionRange> ENTRY1 = VersionRangeSerializerTest.create(
    VersionRange.from( 1, 0, 0 ).to( 1, 9, 17 ), VersionRangeSerializerTest.class.getResource( "VersionRange_1.0.0_1.json" ) );

  @Override
  protected Serializer<VersionRange> getSerializer() throws Exception {
    return new VersionRangeSerializer();
  }

}
