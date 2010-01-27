/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.DateTimeTest;
import com.google.inject.Inject;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
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
  public void testFormatter() {
    verifyFormatter( 9661315153L, "19700422T144155.153-0500" );
    verifyFormatter( 966351113153L, "20000815T105153.153-0400" );
  }

  private void verifyFormatter( long instant, @NotNull @NonNls String text ) {
    assertEquals( DateTimeSerializer.createFormatter().print( instant ), text );
    assertEquals( DateTimeSerializer.createFormatter().parseDateTime( text ).getMillis(), instant );
  }

  @Test
  public void test100() throws IOException, SAXException {
    DateTime deserialized = serializer.deserialize( new ByteArrayInputStream( "<dateTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20010101T010101.001-0500</dateTime>".getBytes() ) );
    assertEquals( deserialized.getMillis(), new DateTime( 2001, 1, 1, 1, 1, 1, 1, zone ).getMillis() );

    assertEquals( zone.getID(), "America/New_York" );
    assertEquals( deserialized.getZone().getID(), "-05:00" );
  }

  @Test
  public void testWrite100() throws IOException, SAXException {
    byte[] serialized = serializer.serializeToByteArray( new DateTime( 2001, 1, 1, 1, 1, 1, 1, zone ) );
    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<dateTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20010101T010101.001-0500</dateTime>" );

    DateTime deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    assertEqualsDateTime( deserialized, new DateTime( 2001, 1, 1, 1, 1, 1, 1, zone ) );

    assertEquals( zone.getID(), "America/New_York" );
    assertEquals( deserialized.getZone().getID(), "-05:00" );
  }

  @Test
  public void testWrite100_2() throws IOException, SAXException {
    DateTime dateTime = new DateTime( 2001, 1, 1, 1, 1, 1, 1, DateTimeZone.forID( "Europe/Berlin" ) );
    byte[] serialized = serializer.serializeToByteArray( dateTime );
    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<dateTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20010101T010101.001+0100</dateTime>" );

    DateTime deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    assertEqualsDateTime( deserialized, new DateTime( 2001, 1, 1, 1, 1, 1, 1, DateTimeZone.forID( "Europe/Berlin" ) ) );

    assertEquals( dateTime.getZone().getID(), "Europe/Berlin" );
    assertEquals( deserialized.getZone().getID(), "+01:00" );
  }

  @Test
  public void testTimezones() throws Exception {
    verifyZone( "Europe/Monaco" );
    verifyZone( "Europe/Berlin" );
    verifyZone( "Europe/Amsterdam" );
    verifyZone( "Atlantic/Faroe" );
    verifyZone( "Africa/Lome" );
    verifyZone( "UTC" );
    verifyZone( "WET" );
  }

  private byte[] verifyZone(@NotNull @NonNls  String id ) throws IOException {
    DateTimeZone zone = DateTimeZone.forID( id );
    assertEquals( zone.getID(), id );

    DateTime dateTime = new DateTime( 2001, 5, 3, 4, 5, 3, 2, zone );
    byte[] serialized = serializer.serializeToByteArray( dateTime );

    DateTime deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    assertEqualsDateTime( dateTime, deserialized );
    return serialized;
  }

  static void assertEqualsDateTime( @NotNull DateTime dateTime, @NotNull DateTime deserialized ) {
    assertEquals( deserialized.getMillis(), dateTime.getMillis(), deserialized + " vs " + dateTime );
    assertEquals( deserialized.getZone().getOffset( deserialized.getMillis() ), dateTime.getZone().getOffset( deserialized.getMillis() ) );
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

    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<dateTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20091231T235901.999-0500</dateTime>" );

    DateTime deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    assertEquals( deserialized.getMillis(), dateTime.getMillis() );
  }

  @Test
  public void testLegacy() throws IOException {
    assertNotNull( serializer );
    assertNotNull( zone );

    DateTimeZone oldDefault = DateTimeZone.getDefault();

    try {
      DateTimeZone.setDefault( zone );

      assertEquals( serializer.deserialize( new ByteArrayInputStream( "<startTime xmlns=\"http://www.joda.org/time/dateTime/0.9.0\">1245859619998</startTime>".getBytes() ) ).withZone( DateTimeZone.UTC ), new DateTime( 2009, 6, 24, 16, 6, 59, 998, DateTimeZone.UTC ) );
    } finally {
      DateTimeZone.setDefault( oldDefault );
    }
  }
}
