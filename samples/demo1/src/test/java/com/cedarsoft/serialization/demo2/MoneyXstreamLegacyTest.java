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

package com.cedarsoft.serialization.demo2;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

import static com.cedarsoft.AssertUtils.assertXMLEqual;
import static org.testng.Assert.*;

/**
 *
 */
public class MoneyXstreamLegacyTest {
  private XStream xStream;

  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
  }

  @Test
  public void testFromXml() throws IOException {
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream() ) ).getCents(), 701 );
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream() ) ).getAmount(), 7.01 );
  }

  @Test
  public void testFromXmlLegacyFormat() throws IOException {
    try {
      xStream.fromXML( com.cedarsoft.serialization.demo1.MoneyTest.EXPECTED.openStream() );
      fail( "Where is the Exception" );
    } catch ( ConversionException e ) {
      assertEquals( e.getMessage(), "amount : amount : amount : amount\n" +
        "---- Debugging information ----\n" +
        "message             : amount : amount\n" +
        "cause-exception     : com.thoughtworks.xstream.mapper.CannotResolveClassException\n" +
        "cause-message       : amount : amount\n" +
        "class               : com.cedarsoft.serialization.demo2.Money\n" +
        "required-type       : com.cedarsoft.serialization.demo2.Money\n" +
        "path                : /money/amount\n" +
        "line number         : 3\n" +
        "-------------------------------" );
    }
  }

  @Test
  public void testCustomConverter() throws IOException, SAXException {
    xStream.registerConverter( new MoneyConverter() );
    //writing
    assertXMLEqual( xStream.toXML( new Money( 701 ) ), com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED );
    //current format
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream() ) ).getCents(), 701 );
    //old format
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo1.MoneyTest.EXPECTED.openStream() ) ).getCents(), 701 );
  }

  public static class MoneyConverter implements Converter {
    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
      writer.startNode( "cents" );
      writer.setValue( String.valueOf( ( ( Money ) source ).getCents() ) );
      writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
      reader.moveDown();
      long cents;
      if ( reader.getNodeName().equals( "amount" ) ) {
        //Legacy!
        cents = Money.convertValueToCents( Double.parseDouble( reader.getValue() ) );
      } else {
        cents = Long.parseLong( reader.getValue() );
      }
      reader.getValue();
      reader.moveUp();

      return new Money( cents );
    }

    @Override
    public boolean canConvert( Class type ) {
      return type.equals( Money.class );
    }
  }

  @Test
  public void testManual() throws XMLStreamException, IOException, SAXException {
    assertEquals( deserialize( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream(), Version.CURRENT ).getCents(), 701 );
    assertEquals( deserialize( com.cedarsoft.serialization.demo1.MoneyTest.EXPECTED.openStream(), Version.LEGACY ).getCents(), 701 );
  }

  private static Money deserialize( @NotNull InputStream serialized, Version version ) throws XMLStreamException {
    //Boilerplate
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader( serialized );
    //    writer.writeStartDocument();

    //That is the actual code
    reader.nextTag();//money
    reader.nextTag();//cents or amount
    reader.next();//the content

    long cents;
    if ( version == Version.LEGACY ) {
      cents = Money.convertValueToCents( Double.parseDouble( reader.getText() ) );
    } else {
      cents = Long.parseLong( reader.getText() );
    }

    return new Money( cents );
  }

  public enum Version {
    LEGACY,
    CURRENT
  }
}
