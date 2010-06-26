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

package com.cedarsoft.serialization.demo1;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 *
 */
public class MoneyTest {
  public static final int COUNT = 1000;
  private XStream xStream;
  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
    xStream.useAttributeFor( Money.class, "cents" );
  }

  @Test
  public void testXStream() {
    assertEquals( xStream.toXML( new Money( 11351 ) ), "<money cents=\"11351\"/>" );
  }

  @Test
  public void testSimple() {
    assertEquals( serialize( new Money( 11351 ) ), "<money cents=\"11351\"/>" );
  }

  private static String serialize( @NotNull Money money ) {
    return "<money cents=\"" + money.getCents() + "\"/>";
  }

  public static void main( String[] args ) throws Exception {
    System.out.println( "---------" );

//    {
//      MoneyTest test = new MoneyTest();
//      long start = System.currentTimeMillis();
//      for ( int i = 0; i < COUNT; i++ ) {
//        test.testSimple();
//      }
//      System.out.println( "Simple took: " + ( System.currentTimeMillis() - start ) + "ms" );
//    }

    {
      MoneyTest test = new MoneyTest();
      test.setUp();
      long start = System.currentTimeMillis();
      for ( int i = 0; i < COUNT; i++ ) {
        test.testXStream();
      }
      System.out.println( "XStream took: " + ( System.currentTimeMillis() - start ) + "ms" );
    }

  }
}
