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

import com.cedarsoft.serialization.test.performance.jaxb.Extension;
import com.cedarsoft.serialization.test.performance.jaxb.FileType;
import com.cedarsoft.test.utils.AssertUtils;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class JaxbTest {
  private JAXBContext context;

  @Before
  public void setUp() throws Exception {
    context = JAXBContext.newInstance( FileType.class );
  }

  @Test
  public void testSimple() throws JAXBException, IOException, SAXException {
    FileType type = new FileType( "jpg", new Extension( ".", "jpg", true ), false );

    Marshaller marshaller = context.createMarshaller();

    marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    marshaller.marshal( type, out );

    AssertUtils.assertXMLEquals(out.toString(),
                                "<ns2:fileType xmlns:ns2=\"http://test.cedarsoft.com/fileType\">\n" +
                                  "    <dependent>false</dependent>\n" +
                                  "    <extension>\n" +
                                  "        <default>true</default>\n" +
                                  "        <delimiter>.</delimiter>\n" +
                                  "        <extension>jpg</extension>\n" +
                                  "    </extension>\n" +
                                  "    <id>jpg</id>\n" +
                                  "</ns2:fileType>");
  }

  @Test
  public void testUnmarsh() throws JAXBException {
    Unmarshaller unmarshaller = context.createUnmarshaller();

    FileType deserialized = (com.cedarsoft.serialization.test.performance.jaxb.FileType) unmarshaller.unmarshal( new StringReader( XmlParserPerformance.CONTENT_SAMPLE_JAXB ) );
    assertNotNull( deserialized );
  }
}
