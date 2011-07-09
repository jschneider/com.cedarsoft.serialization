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

import com.cedarsoft.serialization.stax.StaxSupport;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.junit.*;

import javax.xml.stream.XMLStreamReader;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StaxMateSupportTest {
  @Before
  public void setup() {
    clear();
  }

  @After
  public void testDown() {
    clear();
  }

  private void clear() {
    StaxMateSupport.clear();
  }

  @Test
  public void testEventNames() {
    Assert.assertEquals("START_ELEMENT", StaxSupport.getEventName(XMLStreamReader.START_ELEMENT));
    assertEquals( "DTD", StaxSupport.getEventName( XMLStreamReader.DTD ) );
    assertEquals( "END_ELEMENT", StaxSupport.getEventName( XMLStreamReader.END_ELEMENT ) );
    assertEquals( "6151351", StaxSupport.getEventName( 6151351 ) );
  }

  @Test
  public void testFactories() {
    assertEquals( WstxInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );

    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    assertEquals( MappedXMLInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );

    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );
    assertEquals( MappedXMLOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );
  }

  @Test
  public void testFactories2() {
    assertEquals( WstxInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );

    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    assertEquals( MappedXMLInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );

    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );
    assertEquals( MappedXMLOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );
  }

  @Test
  public void testStaxMate() throws Exception {
    assertEquals( SMInputFactory.class, StaxMateSupport.getSmInputFactory().getClass() );
    assertEquals( SMOutputFactory.class, StaxMateSupport.getSmOutputFactory().getClass() );

    assertEquals( WstxInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );
  }

  @Test
  public void testStaxMateChangeAfter() throws Exception {
    assertEquals( WstxInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );

    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    assertEquals( MappedXMLInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );

    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );
    assertEquals( MappedXMLOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );

    assertEquals( WstxInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );
  }

  @Test
  public void testStaxMateChangeBefore() throws Exception {
    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );

    assertEquals( MappedXMLInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( MappedXMLOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );
  }
}
