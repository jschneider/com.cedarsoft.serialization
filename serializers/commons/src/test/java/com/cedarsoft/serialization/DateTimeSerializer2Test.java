package com.cedarsoft.serialization;

import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializerTest;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.*;
import org.testng.annotations.*;

/**
 *
 */
public class DateTimeSerializer2Test extends AbstractStaxMateSerializerTest<DateTime> {
  @NotNull
  protected final DateTimeZone zone = DateTimeZone.forID( "America/New_York" );

  private DateTimeZone oldTimeZone;

  @BeforeMethod
  protected void setUpDateTimeZone() throws Exception {
    oldTimeZone = DateTimeZone.getDefault();
    DateTimeZone.setDefault( zone );
  }

  @AfterMethod
  protected void tearDownDateTimeZone() {
    DateTimeZone.setDefault( oldTimeZone );
  }

  @NotNull
  @Override
  protected AbstractStaxMateSerializer<DateTime> getSerializer() {
    return new DateTimeSerializer();
  }

  @NotNull
  @Override
  protected DateTime createObjectToSerialize() {
    return new DateTime( 2009, 5, 1, 2, 2, 5, 4 );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<dateTime>20090501T020205.004-0400</dateTime>";
  }

  @Override
  protected void verifyDeserialized( @NotNull DateTime dateTime ) {
    Assert.assertEquals( dateTime, createObjectToSerialize() );
  }
}
