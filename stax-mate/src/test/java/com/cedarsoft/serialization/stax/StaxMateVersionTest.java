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

package com.cedarsoft.serialization.stax;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 */
public class StaxMateVersionTest {
  @Test
  public void testOld() throws IOException, SAXException {
    OldIntegerSerializer serializer = new OldIntegerSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );
    AssertUtils.assertXMLEquals( out.toString(), "<integer xmlns=\"http://integer/1.0.0\" value=\"7\" />" );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), Integer.valueOf( 7 ) );
  }

  @Test
  public void testNew() throws IOException, SAXException {
    NewIntegerSerializer serializer = new NewIntegerSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );
    AssertUtils.assertXMLEquals( out.toString(), "<integer xmlns=\"http://integer/2.0.0\">7</integer>" );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), Integer.valueOf( 7 ) );
  }


  @Test
  public void testVersionsFail() throws IOException {
    OldIntegerSerializer serializer = new OldIntegerSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );

    try {
      new NewIntegerSerializer().deserialize( new ByteArrayInputStream( out.toByteArray() ) );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException e ) {
      assertEquals( e.getActual(), new Version( 1, 0, 0 ) );
      assertEquals( e.getExpected(), new Version( 2, 0, 0 ) );
    }

    assertEquals( new NewOldSerializer().deserialize( new ByteArrayInputStream( out.toByteArray() ) ), Integer.valueOf( 7 ) );
  }


  public static class NewOldSerializer extends AbstractStaxMateSerializer<Integer> {
    public NewOldSerializer() {
      super( "integer", "http://integer", new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      serializeTo.addCharacters( object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      if ( formatVersion.equals( new Version( 1, 0, 0 ) ) ) {
        int intValue = Integer.parseInt( deserializeFrom.getAttributeValue( null, "value" ) );
        closeTag( deserializeFrom );
        return intValue;
      } else if ( formatVersion.equals( new Version( 2, 0, 0 ) ) ) {
        return Integer.parseInt( getText( deserializeFrom ) );
      } else {
        throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
      }
    }
  }

  public static class NewIntegerSerializer extends AbstractStaxMateSerializer<Integer> {
    public NewIntegerSerializer() {
      super( "integer", "http://integer", new VersionRange( new Version( 2, 0, 0 ), new Version( 2, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      serializeTo.addCharacters( object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      return Integer.parseInt( getText( deserializeFrom ) );
    }
  }

  public static class OldIntegerSerializer extends AbstractStaxMateSerializer<Integer> {
    public OldIntegerSerializer() {
      super( "integer", "http://integer", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.addAttribute( "value", object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      int intValue = Integer.parseInt( deserializeFrom.getAttributeValue( null, "value" ) );
      closeTag( deserializeFrom );
      return intValue;
    }
  }
}
