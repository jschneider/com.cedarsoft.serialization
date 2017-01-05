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

package com.cedarsoft.serialization.test.performance;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.junit.*;

import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JsonTest {
  @Test
  public void testIt() throws Exception {
    StringWriter strWriter = new StringWriter();

    // Mapped convention
    MappedNamespaceConvention con = new MappedNamespaceConvention();
    XMLStreamWriter w = new MappedXMLStreamWriter( con, strWriter );
    // XMLStreamWriter w = new BadgerFishXMLStreamWriter(strWriter);

    w.writeStartDocument();

    w.writeStartElement( "fileType" );
    w.writeAttribute( "dependent", "false" );

    w.writeStartElement( "id" );
    w.writeCharacters( "Canon Raw" );
    w.writeEndElement();

    w.writeStartElement( "extension" );
    w.writeAttribute( "default", "true" );
    w.writeAttribute( "delimiter", "." );
    w.writeCharacters( "cr2" );
    w.writeEndElement();

    w.writeEndElement();
    w.writeEndDocument();

    w.close();
    strWriter.close();

    assertEquals( "{\"fileType\":{\"@dependent\":false,\"id\":\"Canon Raw\",\"extension\":{\"@default\":true,\"@delimiter\":\".\",\"$\":\"cr2\"}}}", strWriter.toString() );
  }

  @Test
  public void testBadger() throws Exception {

    StringWriter strWriter = new StringWriter();

    // Mapped convention
    XMLStreamWriter w = new BadgerFishXMLStreamWriter( strWriter );
    // XMLStreamWriter w = new BadgerFishXMLStreamWriter(strWriter);

    w.writeStartDocument();

    w.writeStartElement( "fileType" );
    w.writeAttribute( "dependent", "false" );

    w.writeStartElement( "id" );
    w.writeCharacters( "Canon Raw" );
    w.writeEndElement();

    w.writeStartElement( "extension" );
    w.writeAttribute( "default", "true" );
    w.writeAttribute( "delimiter", "." );
    w.writeCharacters( "cr2" );
    w.writeEndElement();

    w.writeEndElement();
    w.writeEndDocument();

    w.close();
    strWriter.close();

    assertEquals( "{\"fileType\":{\"@dependent\":\"false\",\"id\":{\"$\":\"Canon Raw\"},\"extension\":{\"@default\":\"true\",\"@delimiter\":\".\",\"$\":\"cr2\"}}}", strWriter.toString() );

  }

  //
  //  "<fileType dependent=\"false\">\n" +
  //  "  <id>Canon Raw</id>\n" +
  //  "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
  //  "</fileType>";

}
