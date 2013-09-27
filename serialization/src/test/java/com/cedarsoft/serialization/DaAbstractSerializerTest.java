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
import com.cedarsoft.version.VersionMismatchException;
import com.cedarsoft.version.VersionRange;
import org.apache.commons.io.Charsets;
import org.junit.*;
import org.junit.rules.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 */
public class DaAbstractSerializerTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private MySerializer serializer;

  @Before
  public void setUp() throws Exception {
    serializer = new MySerializer();
  }

  @Test
  public void testSeria() throws Exception {
    assertThat( new String( serializer.serializeToByteArray( "daObject" ), Charsets.UTF_8 ), is( "daObject" ) );
  }

  @Test
  public void testDeserialize() throws IOException {
    assertThat( serializer.deserialize( "asdf", Version.valueOf( 7, 0, 0 ) ), is( "asdf" ) );
  }

  @Test
  public void testWritable() {
    assertTrue( serializer.isVersionWritable( Version.valueOf( 2, 0, 0 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 1, 0, 0 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 1, 5, 0 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 2, 0, 1 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 0, 0, 1 ) ) );

    serializer.verifyVersionWritable( Version.valueOf( 2, 0, 0 ) );

    expectedException.expect( VersionMismatchException.class );
    expectedException.expectMessage( "Version mismatch. Expected [2.0.0] but was [2.0.1]" );
    serializer.verifyVersionWritable( Version.valueOf( 2, 0, 1 ) );
  }

  @Test
  public void testReadable() {
    assertTrue( serializer.isVersionReadable( Version.valueOf( 1, 0, 0 ) ) );
    assertTrue( serializer.isVersionReadable( Version.valueOf( 1, 5, 0 ) ) );
    assertTrue( serializer.isVersionReadable( Version.valueOf( 2, 0, 0 ) ) );
    assertFalse( serializer.isVersionReadable( Version.valueOf( 2, 0, 1 ) ) );
    assertFalse( serializer.isVersionReadable( Version.valueOf( 0, 0, 1 ) ) );

    serializer.verifyVersionReadable( Version.valueOf( 1, 0, 0 ) );
    serializer.verifyVersionReadable( Version.valueOf( 2, 0, 0 ) );

    expectedException.expect( VersionMismatchException.class );
    expectedException.expectMessage( "Version mismatch. Expected [1.0.0-2.0.0] but was [2.0.1]" );
    serializer.verifyVersionReadable( Version.valueOf( 2, 0, 1 ) );
  }

  @Test
  public void testIt() throws Exception {
    serializer.verifyVersionReadable( Version.valueOf( 1, 0, 0 ) );
    serializer.verifyVersionReadable( Version.valueOf( 1, 5, 0 ) );
    serializer.verifyVersionReadable( Version.valueOf( 2, 0, 0 ) );

    expectedException.expect( VersionMismatchException.class );
    expectedException.expectMessage( "Version mismatch. Expected [1.0.0-2.0.0] but was [2.0.1]" );

    serializer.verifyVersionReadable( Version.valueOf( 2, 0, 1 ) );
  }

  @Test
  public void testIt2() throws Exception {
    serializer.verifyVersionReadable( Version.valueOf( 1, 0, 0 ) );
    serializer.verifyVersionReadable( Version.valueOf( 1, 5, 0 ) );
    serializer.verifyVersionReadable( Version.valueOf( 2, 0, 0 ) );

    expectedException.expect( VersionMismatchException.class );
    expectedException.expectMessage( "Version mismatch. Expected [1.0.0-2.0.0] but was [0.99.99]" );

    serializer.verifyVersionReadable( Version.valueOf( 0, 99, 99 ) );
  }

  @Test
  public void testVerify() throws Exception {
    assertThat( serializer.getFormatVersion(), is( Version.valueOf( 2, 0, 0 ) ) );
    assertThat( serializer.getFormatVersionRange(), is( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) ) );

    expectedException.expect( VersionException.class );
    expectedException.expectMessage( "No mappings available" );

    serializer.getDelegatesMappings().verify();
  }

  public static class MySerializer extends AbstractStreamSerializer<String, StringBuffer, String, IOException> {
    public MySerializer() {
      super( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );
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
