package com.cedarsoft.serialization.json;

import com.cedarsoft.license.License;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.junit.experimental.theories.*;

public class LicenseSerializerTest extends AbstractJsonSerializerTest2<License> {
  @DataPoint
  public static final Entry<? extends License> ENTRY1 = LicenseSerializerTest.create( License.GPL_3, LicenseSerializerTest.class.getResource( "License_1.0.0_1.json" ) );
  @DataPoint
  public static final Entry<? extends License> ENTRY_NULL_URL = LicenseSerializerTest.create(
    new License( "daId", "daName" ), LicenseSerializerTest.class.getResource( "License_1.0.0_nullUrl.json" ) );

  @DataPoint
  public static final Entry<? extends License> ENTRY_CC = LicenseSerializerTest.create(
    License.CC_BY_NC_SA, LicenseSerializerTest.class.getResource( "License_1.0.0_CC.json" ) );

  @Override
  protected Serializer<License> getSerializer() throws Exception {
    return new LicenseSerializer();
  }
}
