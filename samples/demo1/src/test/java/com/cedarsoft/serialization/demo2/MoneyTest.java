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

import com.cedarsoft.test.utils.AssertUtils;
import com.thoughtworks.xstream.XStream;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 */
public class MoneyTest {
  @Nonnull
  public static final URL EXPECTED = MoneyTest.class.getResource( "money.xml" );

  private XStream xStream;

  @Before
  public void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
  }

  @Test
  public void testXStream() throws IOException, SAXException {
    AssertUtils.assertXMLEquals(EXPECTED, xStream.toXML(new Money(7, 1)));
    AssertUtils.assertXMLEquals( EXPECTED, xStream.toXML( new Money( 701 ) ) );
    Assert.assertEquals( 7.01, ( ( Money ) xStream.fromXML( EXPECTED.openStream() ) ).getAmount(), 0 );
    Assert.assertEquals( 701, ( ( Money ) xStream.fromXML( EXPECTED.openStream() ) ).getCents() );
  }

  @Test
  public void testXStreamAttribute() {
    xStream.useAttributeFor( Money.class, "cents" );

    Assert.assertEquals( "<money cents=\"701\"/>", xStream.toXML( new Money( 7, 1 ) ) );
    Assert.assertEquals( "<money cents=\"701\"/>", xStream.toXML( new Money( 701 ) ) );
    Assert.assertEquals( 7.01, ( ( Money ) xStream.fromXML( "<money cents=\"701\"/>" ) ).getAmount(), 0 );
    Assert.assertEquals( 701, ( ( Money ) xStream.fromXML( "<money cents=\"701\"/>" ) ).getCents() );
  }

  @Test
  public void testSimple() throws XMLStreamException, IOException, SAXException {
    AssertUtils.assertXMLEquals( EXPECTED, serialize( new Money( 7, 1 ) ) );
    AssertUtils.assertXMLEquals( EXPECTED, serialize( new Money( 701 ) ) );

    Assert.assertEquals( 701, deserialize( EXPECTED.openStream() ).getCents() );
  }

  protected static String serialize( @Nonnull Money money ) throws XMLStreamException {
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

  private static Money deserialize( @Nonnull InputStream serialized ) throws XMLStreamException {
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
