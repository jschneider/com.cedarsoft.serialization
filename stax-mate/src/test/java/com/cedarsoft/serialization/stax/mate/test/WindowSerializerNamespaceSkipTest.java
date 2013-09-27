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

package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.junit.*;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 */
public class WindowSerializerNamespaceSkipTest {
  @Nonnull
  protected StreamSerializer<Window> getSerializer() throws Exception {
    return new DaSkippingSerializer();
  }

  @Test
  public void testIt() throws IOException {
    DaSkippingSerializer serializer = new DaSkippingSerializer();
    Window deserialized = serializer.deserialize( new ByteArrayInputStream( (
                                                                              "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                                                                "<window xmlns=\"window/0.9.0\" width=\"123.3\" height=\"444.4\">\n" +
                                                                                "  <other xmlns=\"window/2.0.0\"><a><b>content of b</b></a></other>\n" +
                                                                                "  <other xmlns=\"window/2.0.0\"><a><b>content of b</b></a></other>\n" +
                                                                                "  <other xmlns=\"window/2.0.0\"><a><b>content of b</b></a></other>\n" +
                                                                                "  <description>the window</description>\n" +
                                                                                "  <other xmlns=\"asdf\"/>" +
                                                                                "  <other xmlns=\"asdf2\"/>" +
                                                                                "</window>" ).getBytes() ) );

    assertEquals( "the window", deserialized.getDescription() );
    assertEquals( 444.4, deserialized.getHeight(), 0.0 );
    assertEquals( 123.3, deserialized.getWidth(), 0.0 );
  }

  public static class DaSkippingSerializer extends AbstractStaxMateSerializer<Window> {
    public DaSkippingSerializer() {
      super( "window", "window", new VersionRange( new Version( 0, 9, 0 ), new Version( 0, 9, 0 ) ) );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Window object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );

      serializeTo.addAttribute( "width", String.valueOf( object.getWidth() ) );
      serializeTo.addAttribute( "height", String.valueOf( object.getHeight() ) );

      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );
    }

    @Nonnull
    @Override
    public Window deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      double width = Double.parseDouble( deserializeFrom.getAttributeValue( null, "width" ) );
      double height = Double.parseDouble( deserializeFrom.getAttributeValue( null, "height" ) );

      nextTag( deserializeFrom, "description", getNameSpace() );
      String description = getText( deserializeFrom );
      closeTag( deserializeFrom );

      return new Window( description, width, height );
    }
  }
}
