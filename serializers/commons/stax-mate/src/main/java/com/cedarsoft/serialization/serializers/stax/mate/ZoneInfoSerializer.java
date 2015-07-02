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

package com.cedarsoft.serialization.serializers.stax.mate;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.Serializer;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class ZoneInfoSerializer implements Serializer<DateTimeZone,OutputStream,InputStream> {
  @Override
  public void serialize( @Nonnull DateTimeZone object, @Nonnull OutputStream out ) throws IOException {
    out.write( object.getID().getBytes(StandardCharsets.UTF_8) );
  }

  @Nonnull
  @Override
  public DateTimeZone deserialize( @Nonnull InputStream in ) throws IOException, VersionException {
    return DateTimeZone.forID( IOUtils.toString( in ) );
  }

  @Nonnull
  @Override
  public Version getFormatVersion() {
    return new Version( 1, 0, 0 );
  }

  @Nonnull
  @Override
  public VersionRange getFormatVersionRange() {
    return VersionRange.from( 1, 0, 0 ).to();
  }
}
