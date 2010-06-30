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
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.cedarsoft.AssertUtils.assertXMLEquals;
import static org.testng.Assert.*;

/**
 *
 */
public class MoneyTest {
  @NotNull
  public static final URL EXPECTED = MoneyTest.class.getResource( "money.xml" );

  private XStream xStream;

  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
  }

  @Test
  public void testXStream() throws IOException, SAXException {
    assertXMLEquals( xStream.toXML( new Money( 7, 1 ) ), EXPECTED );
    assertXMLEquals( xStream.toXML( new Money( 701 ) ), EXPECTED );
    assertEquals( ( ( Money ) xStream.fromXML( EXPECTED.openStream() ) ).getAmount(), 7.01 );
    assertEquals( ( ( Money ) xStream.fromXML( EXPECTED.openStream() ) ).getCents(), 701 );
  }

  @Test
  public void testXStreamAttribute() {
    xStream.useAttributeFor( Money.class, "cents" );

    assertEquals( xStream.toXML( new Money( 7, 1 ) ), "<money cents=\"701\"/>" );
    assertEquals( xStream.toXML( new Money( 701 ) ), "<money cents=\"701\"/>" );
    assertEquals( ( ( Money ) xStream.fromXML( "<money cents=\"701\"/>" ) ).getAmount(), 7.01 );
    assertEquals( ( ( Money ) xStream.fromXML( "<money cents=\"701\"/>" ) ).getCents(), 701 );
  }

  @Test
  public void testSimple() throws XMLStreamException, IOException, SAXException {
    assertXMLEquals( serialize( new Money( 7, 1 ) ), EXPECTED );
    assertXMLEquals( serialize( new Money( 701 ) ), EXPECTED );

    assertEquals( deserialize( EXPECTED.openStream() ).getCents(), 701 );
  }

  protected static String serialize( @NotNull Money money ) throws XMLStreamException {
    //Boilerplate
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter( out );
    //    writer.writeStartDocument();

    //That is the actual code
    writer.writeStartElement( "money" );
    writer.writeStartElement( "cents" );
    writer.writeCharacters( String.valueOf( money.getCents() ) );
    writer.writeEndElement();
    writer.writeEndElement();

    //Boiler plate
    writer.close();
    return out.toString();
  }

  private static Money deserialize( @NotNull InputStream serialized ) throws XMLStreamException {
    //Boilerplate
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader( serialized );
    //    writer.writeStartDocument();

    //That is the actual code
    reader.nextTag();//money
    reader.nextTag();//cents
    reader.next();//the content
    long cents = Long.parseLong( reader.getText() );

    return new Money( cents );
  }
}
