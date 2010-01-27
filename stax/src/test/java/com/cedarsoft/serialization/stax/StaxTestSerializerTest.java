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

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxTestSerializerTest extends AbstractXmlSerializerTest<Integer> {
  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<int  xmlns=\"http://int/1.0.0\">7</int>";
  }

  @NotNull
  @Override
  protected Serializer<Integer> getSerializer() {
    return new StaxIntegerSerializer();
  }

  @NotNull
  @Override
  protected Integer createObjectToSerialize() {
    return 7;
  }

  @Test
  public void testIt() {
    assertEquals( XMLInputFactory.newInstance( "com.sun.xml.internal.stream.XMLInputFactoryImpl", getClass().getClassLoader() ).getClass().getName(), "com.sun.xml.internal.stream.XMLInputFactoryImpl" );
    assertEquals( XMLInputFactory.newInstance( "com.sun.xml.internal.stream.XMLInputFactoryImpl", getClass().getClassLoader() ).getClass().getName(), "com.sun.xml.internal.stream.XMLInputFactoryImpl" );
  }


  public static class StaxIntegerSerializer extends AbstractStaxSerializer<Integer> {
    public StaxIntegerSerializer() {
      super( "int", "http://int", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull XMLStreamWriter serializeTo, @NotNull Integer object ) throws IOException, XMLStreamException {
      serializeTo.writeCharacters( object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      return Integer.parseInt( getText( deserializeFrom ) );
    }
  }

}
