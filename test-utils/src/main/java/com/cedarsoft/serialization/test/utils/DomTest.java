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
package com.cedarsoft.serialization.test.utils;

import com.cedarsoft.test.utils.AssertUtils;
import com.cedarsoft.xml.XmlCommons;
import com.sun.org.apache.xerces.internal.dom.DeferredNode;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class DomTest {
  @Test
  public void testIt() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware( true );
    DocumentBuilder documentBuilder = factory.newDocumentBuilder();

    Document doc = documentBuilder.parse( new ByteArrayInputStream( "<a/>".getBytes(StandardCharsets.UTF_8) ) );

    Element element = doc.getDocumentElement();
    assertThat( element ).isNotNull();
    assertThat( element.getTagName() ).isEqualTo( "a" );
    assertThat( element.getNamespaceURI() ).isEqualTo( null );

    element.setAttribute( "daAttr", "daval" );

    element.appendChild( doc.createElementNS( "manuallyChangedChildNS", "DaNewChild" ) );
    element.appendChild( doc.createElement( "child2WithoutNS" ) );

    new XmlNamespaceTranslator()
        .addTranslation( null, "MyNS" )
        .translateNamespaces( doc, false );

    StringWriter out = new StringWriter();
    XmlCommons.out( doc, out );

    AssertUtils.assertXMLEquals( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                                    "<a daAttr=\"daval\" xmlns=\"MyNS\">\n" +
                                                    "  <DaNewChild xmlns=\"manuallyChangedChildNS\"/>\n" +
                                                    "  <child2WithoutNS/>\n" +
                                                    "</a>\n", out.toString() );
  }

}
