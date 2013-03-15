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

package com.cedarsoft.sample.xstream;

import com.cedarsoft.sample.Car;
import com.cedarsoft.sample.Extra;
import com.cedarsoft.sample.Model;
import com.cedarsoft.sample.Money;
import com.cedarsoft.test.utils.AssertUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import org.junit.*;
import org.junit.rules.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 *
 */
public class XStreamDemo {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  //START SNIPPET: createSampleCar
  public Car createSampleCar() {
    Model model = new Model( "Ford" );
    Extra extra0 = new Extra( "Whoo effect", new Money( 99.98 ) );
    Extra extra1 = new Extra( "Better Whoo effect", new Money( 199.00 ) );
    List<Extra> extras = Arrays.asList( extra0, extra1 );
    return new Car( model, new Money( 19000.00 ), extras );
  }
  //END SNIPPET: createSampleCar

  @Test
  public void testIt() throws IOException, SAXException {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );

    String xml = xStream.toXML( createSampleCar() );
    AssertUtils.assertXMLEquals(getClass().getResource("car.xml"), xml);
  }

  @Test
  public void testDeserialize() {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );

    Car deserialized = ( Car ) xStream.fromXML( getClass().getResourceAsStream( "car.xml" ) );
    assertEquals( deserialized.getBasePrice(), createSampleCar().getBasePrice() );
  }

  @Test
  public void testDeserializeWithFixedClasses() {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", com.cedarsoft.sample.fixed.Car.class );
    xStream.alias( "extra", com.cedarsoft.sample.fixed.Extra.class );
    xStream.alias( "money", com.cedarsoft.sample.fixed.Money.class );

    expectedException.expect( ConversionException.class );
    xStream.fromXML( getClass().getResourceAsStream( "car.xml" ) );
  }
}
