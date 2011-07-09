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

import com.cedarsoft.test.utils.AssertUtils;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class StaxTest {
  @Nonnull

  public static final String CONTENT_SAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<fileType dependent=\"false\">\n" +
    "  <id>Canon Raw</id>\n" +
    "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
    "</fileType>";

  @Test
  public void testNameSpace() throws IOException, SAXException, XMLStreamException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = factory.createXMLStreamWriter( out );

    writer.writeStartDocument();

    writer.setDefaultNamespace( "http://namespace" );
    writer.writeStartElement( "fileType" );
    writer.writeDefaultNamespace( "http://namespace" );
    writer.writeAttribute( "dependent", "false" );

    writer.writeStartElement( "id" );
    writer.writeCharacters( "Canon Raw" );
    writer.writeEndElement();

    writer.writeStartElement( "extension" );
    writer.writeAttribute( "default", "true" );
    writer.writeAttribute( "delimiter", "." );
    writer.writeCharacters( "cr2" );
    writer.writeEndElement();

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.close();

    AssertUtils.assertXMLEquals(out.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<fileType xmlns=\"http://namespace\" dependent=\"false\">\n" +
      "  <id>Canon Raw</id>\n" +
      "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
      "</fileType>", true);
  }

  @Test
  public void testCurrentNamespace() throws XMLStreamException, IOException, SAXException {
    XMLOutputFactory factory = XMLOutputFactory.newFactory();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = factory.createXMLStreamWriter( out );

    writer.writeStartDocument();

    writer.setDefaultNamespace( "http://namespace" );
    writer.writeStartElement( "http://namespace", "fileType" );
    writer.writeDefaultNamespace( "http://namespace" );

    writer.writeAttribute( "dependent", "false" );

    writer.writeStartElement( "id" );
    writer.writeEndElement();

    writer.writeEndElement();
    writer.close();

    AssertUtils.assertXMLEquals( out.toString(),
                                 "<fileType xmlns=\"http://namespace\" dependent=\"false\">\n" +
                                   "  <id />\n" +
                                   "</fileType>", true );
  }

  @Test
  public void testBug() throws XMLStreamException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    assertEquals( false, factory.getProperty( XMLOutputFactory.IS_REPAIRING_NAMESPACES ) );
    factory.setProperty( XMLOutputFactory.IS_REPAIRING_NAMESPACES, false );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = factory.createXMLStreamWriter( out );

    assertEquals( false, writer.getProperty( XMLOutputFactory.IS_REPAIRING_NAMESPACES ) );
  }

  @Test
  public void testWrite() throws XMLStreamException, IOException, SAXException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = factory.createXMLStreamWriter( out );

    writer.writeStartDocument();
    writer.writeStartElement( "fileType" );
    writer.writeAttribute( "dependent", "false" );

    writer.writeStartElement( "id" );
    writer.writeCharacters( "Canon Raw" );
    writer.writeEndElement();

    writer.writeStartElement( "extension" );
    writer.writeAttribute( "default", "true" );
    writer.writeAttribute( "delimiter", "." );
    writer.writeCharacters( "cr2" );
    writer.writeEndElement();

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.close();

    AssertUtils.assertXMLEquals( out.toString(), CONTENT_SAMPLE, true );
  }

  @Test
  public void testStax() throws XMLStreamException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader( CONTENT_SAMPLE ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "fileType", parser.getLocalName() );
    assertEquals( "fileType", parser.getName().getLocalPart() );
    assertEquals( "false", parser.getAttributeValue( null, "dependent" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "id", parser.getName().getLocalPart() );
    assertEquals( XMLStreamReader.CHARACTERS, parser.next() );
    assertEquals( "Canon Raw", parser.getText() );
    assertEquals( XMLStreamReader.END_ELEMENT, parser.nextTag() );
    assertEquals( "id", parser.getName().getLocalPart() );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "extension", parser.getName().getLocalPart() );
    assertEquals( "true", parser.getAttributeValue( null, "default" ) );
    assertEquals( ".", parser.getAttributeValue( null, "delimiter" ) );
    assertEquals( XMLStreamReader.CHARACTERS, parser.next() );
    assertEquals( "cr2", parser.getText() );
    assertEquals( XMLStreamReader.END_ELEMENT, parser.nextTag() );
    assertEquals( "extension", parser.getName().getLocalPart() );

    assertEquals( XMLStreamReader.END_ELEMENT, parser.nextTag() );
    assertEquals( "fileType", parser.getName().getLocalPart() );
    assertEquals( XMLStreamReader.END_DOCUMENT, parser.next() );
  }

  @Test
  public void testIterator() throws XMLStreamException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    assertEquals( false, inputFactory.getProperty( XMLInputFactory.IS_COALESCING ) );
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    assertEquals( true, inputFactory.getProperty( XMLInputFactory.IS_COALESCING ) );
    //    inputFactory.setProperty(  );

    XMLEventReader parser = inputFactory.createXMLEventReader( new StringReader( CONTENT_SAMPLE ) );

    {
      XMLEvent event = parser.nextEvent();
      assertEquals( XMLEvent.START_DOCUMENT, event.getEventType() );
    }

    {
      XMLEvent event = parser.nextEvent();
      assertEquals( XMLEvent.START_ELEMENT, event.getEventType() );
      assertEquals( "fileType", event.asStartElement().getName().getLocalPart() );
      assertEquals( "false", event.asStartElement().getAttributeByName( new QName( null, "dependent" ) ).getValue() );
    }
  }
}
