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

package com.cedarsoft.test.io;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerMultiTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.ui.DelegatesMappingVisualizer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Model;
import com.cedarsoft.test.Money;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class CarSerializerTest extends AbstractXmlSerializerMultiTest<Car> {
  @Nonnull
  @Override
  protected StreamSerializer<Car> getSerializer() {
    MoneySerializer moneySerializer = new MoneySerializer();
    //We can share the same serializer. But we don't have to.
    return new CarSerializer( moneySerializer, new ExtraSerializer( moneySerializer ), new ModelSerializer() );
  }

  @Nonnull
  @Override
  protected Iterable<? extends Car> createObjectsToSerialize() {
    return Arrays.asList(
      new Car( new Model( "Toyota" ), Color.BLACK, new Money( 49000, 00 ) ),
      new Car( new Model( "Ford" ), Color.ORANGE, new Money( 19000, 00 ), Arrays.asList( new Extra( "Whoo effect", new Money( 99, 98 ) ), new Extra( "Better Whoo effect", new Money( 199, 00 ) ) ) )
    );
  }

  @Nonnull
  @Override
  protected List<? extends String> getExpectedSerialized() throws Exception {
    return Arrays.asList(
      IOUtils.toString( getClass().getResourceAsStream( "car1.xml" ) ),
      IOUtils.toString( getClass().getResourceAsStream( "car2.xml" ) )
    );
  }

  @Override
  protected void verifyDeserialized( @Nonnull List<? extends Car> deserialized ) {
    //We don't implement equals in the car, therefore compare manually
    //    super.verifyDeserialized( deserialized );

    assertEquals( 2, deserialized.size() );

    Car first = deserialized.get( 0 );
    assertEquals( Color.BLACK, first.getColor() );
    assertEquals( first.getBasePrice(), new Money( 49000, 0 ) );

    //....

  }

  @Test
  public void testAsciiArt() throws IOException {
    CarSerializer serializer = ( CarSerializer ) getSerializer();
    DelegatesMappingVisualizer visualizer = new DelegatesMappingVisualizer( serializer.getDelegatesMappings() );

    assertEquals( visualizer.visualize(),
                  "         -->     Extra     Model     Money\n" +
                    "------------------------------------------\n" +
                    "   1.0.0 -->     1.5.0     1.0.0     1.0.0\n" +
                    "------------------------------------------\n" );
  }
}
