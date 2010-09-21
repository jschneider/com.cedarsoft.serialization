package com.cedarsoft.serialization.json;

import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.experimental.theories.*;

import static org.junit.Assert.*;

/**
 *
 */
public class DateTimeSerializerTest extends AbstractJsonSerializerTest2<DateTime> {
  @NotNull
  protected static final DateTimeZone ZONE = DateTimeZone.forID( "America/New_York" );

  @DataPoint
  public static final Entry<?> entry1 = create( new DateTime( 2009, 5, 1, 2, 2, 5, 4, ZONE ), "\"20090501T020205.004-0400\"" );

  @NotNull
  @Override
  protected Serializer<DateTime> getSerializer() throws Exception {
    return new DateTimeSerializer();
  }

  @Override
  protected boolean addTypeInformation() {
    return false;
  }

  @Override
  protected void verifyDeserialized( @NotNull DateTime deserialized, @NotNull DateTime original ) {
    assertEquals( deserialized.getMillis(), original.getMillis() );
    assertEquals( deserialized.withZone( DateTimeZone.UTC ), deserialized.withZone( DateTimeZone.UTC ) );
    assertEquals( deserialized.withZone( ZONE ), deserialized.withZone( ZONE ) );
  }
}
