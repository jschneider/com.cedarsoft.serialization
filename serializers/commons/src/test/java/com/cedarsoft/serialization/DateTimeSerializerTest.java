package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.DateTimeTest;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.testng.*;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class DateTimeSerializerTest extends DateTimeTest {
  @Inject
  private DateTimeSerializer serializer;

  @BeforeMethod
  public void setup() {
    serializer = new DateTimeSerializer();
  }

  @Test
  public void testA() throws IOException, SAXException {
    byte[] serialized = serializer.serializeToByteArray( new DateTime( 2001, 1, 1, 1, 1, 1, 1, zone ) );
    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<dateTime>20010101T010101.001-0500</dateTime>"
    );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( serialized ) ), new DateTime( 2001, 1, 1, 1, 1, 1, 1, zone ) );
  }

  @Test( enabled = false )
  public void testLoosingTwoSecondsTest() {
    DateTimeZone oldDefault = DateTimeZone.getDefault();
    try {
      DateTimeZone zone = DateTimeZone.forID( "America/New_York" );
      DateTimeZone.setDefault( zone );
      DateTime dateTime = new DateTime( 001, 1, 1, 1, 1, 1, 1, zone );

      DateTimeFormatter format = ISODateTimeFormat.basicDateTime();

      //Compare string pbased
      Assert.assertEquals( format.print( dateTime ), "00010101T010101.001-0456" );

      //Round
      Assert.assertEquals( format.parseDateTime( format.print( dateTime ) ), dateTime );
    } finally {
      DateTimeZone.setDefault( oldDefault );
    }
  }

  @Test
  public void testIt() throws IOException, SAXException {
    DateTime dateTime = new DateTime( 2009, 12, 31, 23, 59, 01, 999, zone );

    byte[] serialized = serializer.serializeToByteArray( dateTime );

    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<dateTime>20091231T235901.999-0500</dateTime>" );

    DateTime deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    Assert.assertEquals( deserialized, dateTime );
  }

  @Test
  public void testLegacy() throws IOException {
    assertNotNull( serializer );
    assertNotNull( zone );

    DateTimeZone oldDefault = DateTimeZone.getDefault();

    try {
      DateTimeZone.setDefault( zone );

      assertEquals( serializer.deserialize( new ByteArrayInputStream( "<?format 1.0.0?><startTime>20090701T140952.653+0200</startTime>".getBytes() ) ), new DateTime( 2009, 07, 01, 8, 9, 52, 653, zone ) );
      assertEquals( serializer.deserialize( new ByteArrayInputStream( "<?format 1.0.0?><startTime>1245859619998</startTime>".getBytes() ) ).withZone( DateTimeZone.UTC ), new DateTime( 2009, 6, 24, 16, 6, 59, 998, DateTimeZone.UTC ) );
    } finally {
      DateTimeZone.setDefault( oldDefault );
    }
  }
}
