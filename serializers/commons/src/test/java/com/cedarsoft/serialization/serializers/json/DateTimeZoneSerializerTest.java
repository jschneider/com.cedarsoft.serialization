package com.cedarsoft.serialization.serializers.json;

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.AbstractSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.joda.time.DateTimeZone;
import org.junit.experimental.theories.DataPoint;

import javax.annotation.Nonnull;

public class DateTimeZoneSerializerTest extends AbstractJsonSerializerTest2<DateTimeZone> {

  @DataPoint
  public static final Entry<? extends DateTimeZone> ENTRY1 = AbstractSerializerTest2.create( DateTimeZone.forID( "Europe/Madrid" ), DateTimeZoneSerializerTest.class.getResource( "DateTimeZone_1.0.0_1.json" ) );

  @Nonnull
  @Override
  protected Serializer<DateTimeZone> getSerializer() throws Exception {
    return new DateTimeZoneSerializer();
  }

}
