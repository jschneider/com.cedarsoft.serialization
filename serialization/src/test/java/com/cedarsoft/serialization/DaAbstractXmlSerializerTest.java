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

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.junit.*;
import org.junit.rules.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 *
 */
public class DaAbstractXmlSerializerTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private MySerializer serializer;

  @Before
  public void setUp() throws Exception {
    serializer = new MySerializer();
  }

  @Test
  public void testNs() throws Exception {
    serializer.verifyNamespace( "nsBase" );
    serializer.verifyNamespace( "nsBaseADDITIONAL" );

    expectedException.expect( SerializationException.class );
    expectedException.expectMessage( "[INVALID_NAME_SPACE] Invalid name space. Expected <nsBase/1.0.0> but was <WrongnsBaseWRONG>." );
    serializer.verifyNamespace( "WrongnsBaseWRONG" );
  }

  @Test
  public void testVerifyNsException() throws Exception {
    serializer.getFormatVersion();

    try {
      serializer.verifyNamespace( "WrongnsBaseWRONG" );
      fail( "Where is the Exception" );
    } catch ( SerializationException e ) {
      assertThat( e ).hasMessage( "[INVALID_NAME_SPACE] Invalid name space. Expected <nsBase/1.0.0> but was <WrongnsBaseWRONG>." );
    }
  }

  public static class MySerializer extends AbstractXmlSerializer<String, StringBuffer, String, IOException> {
    public MySerializer() {
      super( "mu", "nsBase", VersionRange.single( 1, 0, 0 ) );
    }

    @Override
    public void serialize( @Nonnull StringBuffer serializeTo, @Nonnull String object, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      serializeTo.append( object );
    }

    @Nonnull
    @Override
    public String deserialize( @Nonnull String deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      return deserializeFrom;
    }

    @Override
    public void serialize( @Nonnull String object, @Nonnull OutputStream out ) throws IOException {
      out.write( object.getBytes() );
    }

    @Nonnull
    @Override
    public String deserialize( @Nonnull InputStream in ) throws IOException, VersionException {
      throw new UnsupportedOperationException();
    }
  }

}
