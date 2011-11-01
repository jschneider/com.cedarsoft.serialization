package com.cedarsoft.serialization.serializers.json;

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.test.utils.VersionEntry;
import com.cedarsoft.version.Version;
import org.joda.time.DateTimeZone;
import org.junit.experimental.theories.DataPoint;

import javax.annotation.Nonnull;

import static org.fest.assertions.Assertions.assertThat;

public class DateTimeZoneSerializerVersionTest extends AbstractJsonVersionTest2<DateTimeZone> {

  @DataPoint
  public static final VersionEntry ENTRY1 = DateTimeZoneSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), DateTimeZoneSerializerVersionTest.class.getResource( "DateTimeZone_1.0.0_1.json" ) );

  @Nonnull
  @Override
  protected Serializer<DateTimeZone> getSerializer() throws Exception {
    return new DateTimeZoneSerializer();
  }

  @Override
  protected void verifyDeserialized( @Nonnull DateTimeZone deserialized, @Nonnull Version version ) throws Exception {
    assertThat( deserialized.getID() ).isEqualTo( "Europe/Madrid" );
  }

}
