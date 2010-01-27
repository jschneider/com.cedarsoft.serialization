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

package com.cedarsoft.serialization.jdom;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class JdomSerializationTest {
  private MySerializer serializer;

  @BeforeMethod
  protected void setUp() throws Exception {
    serializer = new MySerializer();
  }

  @Test
  public void testIt() throws IOException, SAXException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );

    AssertUtils.assertXMLEqual( out.toString().trim(), "<my  xmlns=\"http://my/1.2.3\">7</my>" );


    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my  xmlns=\"http://my/1.2.3\">7</my>" ).getBytes() );

    assertEquals( serializer.deserialize( in ), new Integer( 7 ) );
  }

  @Test
  public void testWrongVersion() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my  xmlns=\"http://my/1.0.2\">7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }

  @Test
  public void testVersionRange() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my xmlns=\"http://my/1.2.1\">7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }

  @Test
  public void testNoVersion() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my>7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionException ignore ) {
    }
  }

  public static class MySerializer extends AbstractJDomSerializer<Integer> {
    public MySerializer() {
      super( "my", "http://my", new VersionRange( new Version( 1, 2, 1 ), new Version( 1, 2, 3 ) ) );
    }

    @Override
    public void serialize( @NotNull Element serializeTo, @NotNull Integer object ) throws IOException, IOException {
      serializeTo.setText( String.valueOf( object ) );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull Element deserializeFrom, @NotNull Version formatVersion ) throws IOException, IOException {
      return Integer.parseInt( deserializeFrom.getText() );
    }
  }
}
