package com.cedarsoft.serialization;

import com.cedarsoft.serialization.ZoneInfoSerializer;
import org.joda.time.DateTimeZone;

import static org.testng.Assert.*;

import org.testng.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 */
public class ZoneInfoSerializerTest {
  @Test
  public void testRound() throws IOException {
    testRound( DateTimeZone.UTC );
    testRound( DateTimeZone.getDefault() );
    testRound( DateTimeZone.forID( "Europe/Berlin" ) );
    testRound( DateTimeZone.forID( "America/New_York" ) );
  }

  private static void testRound( @NotNull DateTimeZone zone ) throws IOException {
    ZoneInfoSerializer serializer = new ZoneInfoSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( zone, out );

    assertEquals( out.toString(), zone.getID() );

    DateTimeZone deserialized = serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) );
    assertEquals( deserialized, zone );
  }
}
