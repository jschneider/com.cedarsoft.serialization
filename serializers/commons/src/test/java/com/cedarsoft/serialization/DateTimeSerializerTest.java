/**
 * Copyright (C) 2010 cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce.txt
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
    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<dateTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20010101T010101.001-0500</dateTime>" );

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

    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<dateTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20091231T235901.999-0500</dateTime>" );

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

      assertEquals( serializer.deserialize( new ByteArrayInputStream( "<startTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">20090701T140952.653+0200</startTime>".getBytes() ) ), new DateTime( 2009, 07, 01, 8, 9, 52, 653, zone ) );
      assertEquals( serializer.deserialize( new ByteArrayInputStream( "<startTime xmlns=\"http://www.joda.org/time/dateTime/1.0.0\">1245859619998</startTime>".getBytes() ) ).withZone( DateTimeZone.UTC ), new DateTime( 2009, 6, 24, 16, 6, 59, 998, DateTimeZone.UTC ) );
    } finally {
      DateTimeZone.setDefault( oldDefault );
    }
  }
}
