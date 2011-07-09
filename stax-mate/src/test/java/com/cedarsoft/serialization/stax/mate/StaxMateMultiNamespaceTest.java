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

import org.junit.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class StaxMateMultiNamespaceTest {
  @Test
  public void testNs() throws Exception {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader(
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<fileType xmlns=\"dans1\" dependent=\"false\">\n" +
        "  <id>Canon Raw</id>\n" +
        "  <extension xmlns=\"dans2\" default=\"true\" delimiter=\".\">cr2</extension>\n" +
        "</fileType>" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "dans1", parser.getNamespaceURI() );
    assertEquals( "fileType", parser.getLocalName() );
    assertEquals( "fileType", parser.getName().getLocalPart() );
    assertEquals( "false", parser.getAttributeValue( null, "dependent" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "id", parser.getLocalName() );
    assertEquals( "Canon Raw", parser.getElementText() );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "dans2", parser.getNamespaceURI() );
    assertEquals( "extension", parser.getLocalName() );
    assertEquals( "extension", parser.getName().getLocalPart() );
    assertEquals( "true", parser.getAttributeValue( null, "default" ) );
    assertEquals( ".", parser.getAttributeValue( null, "delimiter" ) );
    assertEquals( "cr2", parser.getElementText() );
  }

  @Test
  public void testNs2() throws Exception {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader(
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<fileType xmlns=\"dans1\" dependent=\"false\">\n" +
        "  <id>Canon Raw</id>\n" +
        "  <extension xmlns=\"dans2\" default=\"true\" delimiter=\".\">" +
        "   <asdf/>" +
        " </extension>\n" +
        "</fileType>" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "dans1", parser.getNamespaceURI() );
    assertEquals( "fileType", parser.getLocalName() );
    assertEquals( "fileType", parser.getName().getLocalPart() );
    assertEquals( "false", parser.getAttributeValue( null, "dependent" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "id", parser.getLocalName() );
    assertEquals( "Canon Raw", parser.getElementText() );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "dans2", parser.getNamespaceURI() );
    assertEquals( "extension", parser.getLocalName() );
    assertEquals( "extension", parser.getName().getLocalPart() );
    assertEquals( "true", parser.getAttributeValue( null, "default" ) );
    assertEquals( ".", parser.getAttributeValue( null, "delimiter" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "dans2", parser.getNamespaceURI() );
    assertEquals( "asdf", parser.getLocalName() );
  }
}
