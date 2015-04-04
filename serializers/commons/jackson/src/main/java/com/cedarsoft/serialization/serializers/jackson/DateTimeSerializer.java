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

package com.cedarsoft.serialization.serializers.jackson;

import com.cedarsoft.version.UnsupportedVersionException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

/**
 *
 */
public class DateTimeSerializer extends AbstractJacksonSerializer<DateTime> {
  @Inject
  public DateTimeSerializer() {
    super( "dateTime", new VersionRange( new Version( 0, 9, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull DateTime object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeString( createFormatter().print( object ) );
  }

  @Nonnull
  @Override
  public DateTime deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionReadable( formatVersion );
    String text = deserializeFrom.getText();

    if ( formatVersion.equals( Version.valueOf( 0, 9, 0 ) ) ) {
      return new DateTime( Long.parseLong( text ) );
    }

    if ( formatVersion.equals( Version.valueOf( 1, 0, 0 ) ) ) {
      return createFormatter().withOffsetParsed().parseDateTime( text );
    }

    throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
  }

  @Override
  public boolean isObjectType() {
    return false;
  }

  @Nonnull
  static DateTimeFormatter createFormatter() {
    return ISODateTimeFormat.basicDateTime();
  }
}
