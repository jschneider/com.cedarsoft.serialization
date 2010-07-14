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
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.SerializationContext;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 */
public class DelegatingJDomSerializerTest extends AbstractXmlSerializerTest<Number> {
  private MySerializer serializer;

  @Before
  public void setUp() throws Exception {
    AbstractJDomSerializingStrategy<Integer> intSerializer = new AbstractJDomSerializingStrategy<Integer>( "int", "http://int", Integer.class, new VersionRange( new Version( 1, 0, 1 ), new Version( 1, 0, 1 ) ) ) {
      @Override
      public void serialize( @NotNull Element serializeTo, @NotNull Integer object, SerializationContext context ) throws IOException {
        serializeTo.setText( object.toString() );
      }

      @Override
      @NotNull
      public Integer deserialize( @NotNull @NonNls Element deserializeFrom, @NotNull Version formatVersion, DeserializationContext context ) throws IOException {
        return 1;
      }
    };
    AbstractJDomSerializingStrategy<Double> doubleSerializer = new AbstractJDomSerializingStrategy<Double>( "double", "http://double", Double.class, new VersionRange( new Version( 1, 0, 2 ), new Version( 1, 0, 2 ) ) ) {
      @Override
      public void serialize( @NotNull Element serializeTo, @NotNull Double object, SerializationContext context ) throws IOException {
        serializeTo.setText( object.toString() );
      }

      @Override
      @NotNull
      public Double deserialize( @NotNull @NonNls Element deserializeFrom, @NotNull Version formatVersion, DeserializationContext context ) throws IOException {
        return 2.0;
      }
    };
    serializer = new MySerializer( intSerializer, doubleSerializer );
  }

  @NotNull
  @Override
  protected AbstractJDomSerializer<Number> getSerializer() {
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
    return "<number type=\"int\">1</number>";
  }

  @Override
  protected void verifyDeserialized( @NotNull Number deserialized ) {
    assertEquals( 1, deserialized );
  }

  @Test
  public void testIt() throws IOException, SAXException {
    assertEquals( 2, serializer.getStrategies().size() );

    AssertUtils.assertXMLEquals( new String( serializer.serializeToByteArray( 1 ) ).trim(), "<number xmlns=\"http://number/1.2.3\" type=\"int\">1</number>" );
    AssertUtils.assertXMLEquals( new String( serializer.serializeToByteArray( 2.0 ) ).trim(), "<number xmlns=\"http://number/1.2.3\" type=\"double\">2.0</number>" );
  }

  public static class MySerializer extends AbstractDelegatingJDomSerializer<Number> {
    public MySerializer( @NotNull JDomSerializingStrategy<? extends Number>... serializingStrategies ) {
      super( "number", "http://number", new VersionRange( new Version( 1, 2, 3 ), new Version( 1, 2, 3 ) ), serializingStrategies );
    }
  }
}
