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

import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.*;

/**
 *
 */
public class DateTimeSerializer2Test extends AbstractXmlSerializerTest<DateTime> {
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
    DateTimeSerializerTest.assertEqualsDateTime( dateTime, createObjectToSerialize() );
  }
}
