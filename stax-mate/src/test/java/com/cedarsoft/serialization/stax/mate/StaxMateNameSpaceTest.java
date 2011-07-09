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
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.junit.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class StaxMateNameSpaceTest {
  @Test
  public void testBasic() throws Exception {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    doc.setIndentation( "\n  ", 1, 2 );

    SMNamespace namespace = doc.getNamespace( "http://www.cedarsoft.com/serialization/filetype/1.0.1" );
    SMOutputElement fileTypeElement = doc.addElement( namespace, "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( namespace, "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( namespace, "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    AssertUtils.assertXMLEquals(out.toString(),
                                "<fileType xmlns=\"http://www.cedarsoft.com/serialization/filetype/1.0.1\" dependent=\"false\">\n" +
                                  "  <id>Canon Raw</id>\n" +
                                  "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
                                  "</fileType>", false);


    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader( out.toString() ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "fileType", parser.getLocalName() );
    assertEquals( "http://www.cedarsoft.com/serialization/filetype/1.0.1", parser.getNamespaceURI() );
    assertEquals( "fileType", parser.getName().getLocalPart() );
    assertEquals( "false", parser.getAttributeValue( null, "dependent" ) );

    assertEquals( XMLStreamReader.START_ELEMENT, parser.nextTag() );
    assertEquals( "id", parser.getLocalName() );
    assertEquals( "http://www.cedarsoft.com/serialization/filetype/1.0.1", parser.getNamespaceURI() );
    assertEquals( "id", parser.getName().getLocalPart() );
  }
}
