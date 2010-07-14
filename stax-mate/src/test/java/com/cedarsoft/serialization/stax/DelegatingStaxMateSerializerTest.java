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
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.SerializationContext;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 *
 */
public class DelegatingStaxMateSerializerTest extends AbstractXmlSerializerTest<Number> {
  private MySerializer serializer;

  @Before
  public void setUp() throws Exception {
    AbstractStaxMateSerializingStrategy<Integer> intSerializer = new AbstractStaxMateSerializingStrategy<Integer>( "int", "asdf", Integer.class, new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object, @NotNull Version formatVersion, @NotNull SerializationContext context ) throws IOException, XMLStreamException {
        serializeTo.addCharacters( object.toString() );

      }

      @Override
      @NotNull
      public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException, XMLStreamException {
        assert isVersionReadable( formatVersion );
        getText( deserializeFrom );
        return 1;
      }
    };

    AbstractStaxMateSerializingStrategy<Double> doubleSerializer = new AbstractStaxMateSerializingStrategy<Double>( "double", "asdf", Double.class, new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Double object, @NotNull Version formatVersion, @NotNull SerializationContext context ) throws IOException, XMLStreamException {
        assert isVersionWritable( formatVersion );
        serializeTo.addCharacters( object.toString() );

      }

      @Override
      @NotNull
      public Double deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException, XMLStreamException {
        assert isVersionReadable( formatVersion );
        getText( deserializeFrom );
        return 2.0;
      }
    };
    serializer = new MySerializer( intSerializer, doubleSerializer );
  }

  @NotNull
  @Override
  protected AbstractStaxMateSerializer<Number> getSerializer() {
    return serializer;
  }

  @NotNull
  @Override
  protected Number createObjectToSerialize() {
    return 1;
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<number xmlns=\"http://number/1.0.0\" type=\"int\">1</number>";
  }

  @Override
  protected void verifyDeserialized( @NotNull Number deserialized ) {
    assertEquals( 1, deserialized );
  }

  @Test
  public void testIt() throws IOException, SAXException {
    Assert.assertEquals( 2, serializer.getStrategies().size() );

    AssertUtils.assertXMLEquals( new String( serializer.serializeToByteArray( 1 ) ).trim(), "<number xmlns=\"http://number/1.0.0\" type=\"int\">1</number>" );
    AssertUtils.assertXMLEquals( new String( serializer.serializeToByteArray( 2.0 ) ).trim(), "<number xmlns=\"http://number/1.0.0\" type=\"double\">2.0</number>" );
  }


  public static class MySerializer extends AbstractDelegatingStaxMateSerializer<Number> {
    public MySerializer( @NotNull StaxMateSerializingStrategy<? extends Number>... serializingStrategies ) {
      super( "number", "http://number", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ), serializingStrategies );
    }

    public MySerializer( @NotNull Collection<? extends StaxMateSerializingStrategy<? extends Number>> serializingStrategies ) {
      super( "number", "http://number", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ), serializingStrategies );
    }
  }
}
