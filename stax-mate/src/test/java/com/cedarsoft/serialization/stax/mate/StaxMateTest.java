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

package com.cedarsoft.serialization.stax.mate;

import com.cedarsoft.test.utils.AssertUtils;
import org.codehaus.jettison.badgerfish.BadgerFishXMLOutputFactory;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 *
 */
public class StaxMateTest {
  @Nonnull

  public static final String CONTENT_SAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<fileType dependent=\"false\">\n" +
    "  <id>Canon Raw</id>\n" +
    "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
    "</fileType>";

  @Test
  public void testProcssingInstructions() throws Exception {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    doc.setIndentation( "\n  ", 1, 2 );
    doc.addProcessingInstruction( "format", "version=\"1.0\"" );

    SMOutputElement fileTypeElement = doc.addElement( "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    AssertUtils.assertXMLEquals(out.toString(), CONTENT_SAMPLE, false);
    assertTrue( out.toString(), out.toString().contains( "<?format version=\"1.0\"?>" ) );


    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader( out.toString() ) );

    assertEquals( XMLStreamReader.PROCESSING_INSTRUCTION, parser.next() );
    assertEquals( "format", parser.getPITarget() );
    assertEquals( "version=\"1.0\"", parser.getPIData() );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "fileType", parser.getLocalName() );
    assertEquals( "fileType", parser.getName().getLocalPart() );
    assertEquals( "false", parser.getAttributeValue( null, "dependent" ) );
  }

  @Test
  public void testStaxMate() throws XMLStreamException, IOException, SAXException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    doc.setIndentation( "\n  ", 1, 2 );

    SMOutputElement fileTypeElement = doc.addElement( "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    AssertUtils.assertXMLEquals( out.toString(), CONTENT_SAMPLE, false );
  }

  @Test
  public void testStaxMateRead() throws XMLStreamException {
    SMInputFactory smInputFactory = new SMInputFactory( XMLInputFactory.newInstance() );
    //    XMLStreamReader reader = smInputFactory.createStax2Reader( new StringReader( CONTENT_SAMPLE ) );
    XMLStreamReader reader = smInputFactory.getStaxFactory().createXMLStreamReader( new StringReader( CONTENT_SAMPLE ) );

    assertEquals( XMLStreamReader.START_ELEMENT, reader.nextTag() );
    assertEquals( "fileType", reader.getLocalName() );
    assertEquals( "fileType", reader.getName().getLocalPart() );
    assertEquals( "false", reader.getAttributeValue( null, "dependent" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, reader.nextTag() );
    assertEquals( "id", reader.getName().getLocalPart() );
    assertEquals( XMLStreamReader.CHARACTERS, reader.next() );
    assertEquals( "Canon Raw", reader.getText() );
    assertEquals( XMLStreamReader.END_ELEMENT, reader.nextTag() );
    assertEquals( "id", reader.getName().getLocalPart() );

    assertEquals( XMLStreamReader.START_ELEMENT, reader.nextTag() );
    assertEquals( "extension", reader.getName().getLocalPart() );
    assertEquals( "true", reader.getAttributeValue( null, "default" ) );
    assertEquals( ".", reader.getAttributeValue( null, "delimiter" ) );
    assertEquals( XMLStreamReader.CHARACTERS, reader.next() );
    assertEquals( "cr2", reader.getText() );
    assertEquals( XMLStreamReader.END_ELEMENT, reader.nextTag() );
    assertEquals( "extension", reader.getName().getLocalPart() );

    assertEquals( XMLStreamReader.END_ELEMENT, reader.nextTag() );
    assertEquals( "fileType", reader.getName().getLocalPart() );
    assertEquals( XMLStreamReader.END_DOCUMENT, reader.next() );

    try {
      reader.next();
      fail( "Where is the Exception" );
    } catch ( NoSuchElementException ignore ) {
    }
  }

  @Test
  public void testStaxMateJson() throws XMLStreamException, IOException, SAXException {
    XMLOutputFactory factory = new BadgerFishXMLOutputFactory();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    SMOutputElement fileTypeElement = doc.addElement( doc.getNamespace( "http://cedarsoft.com/test/an/object/1.0.0" ), "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    assertEquals( "{\"fileType\":{\"@xmlns\":{\"$\":\"http:\\/\\/cedarsoft.com\\/test\\/an\\/object\\/1.0.0\"},\"@dependent\":\"false\",\"id\":{\"@xmlns\":{\"$\":\"\"},\"$\":\"Canon Raw\"},\"extension\":{\"@xmlns\":{\"$\":\"\"},\"@default\":\"true\",\"@delimiter\":\".\",\"$\":\"cr2\"}}}", out.toString() );
  }
}
