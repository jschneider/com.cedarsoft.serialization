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

package com.cedarsoft.serialization.ui;

import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.ToString;
import com.cedarsoft.serialization.VersionMappings;
import org.junit.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Comparator;

import static org.junit.Assert.*;

/**
 *
 */
public class VersionMappingsVisualizerTest {
  private VersionRange mine;
  private VersionMappings<Class<?>> versionMappings;
  private VersionRange other;

  @Before
  public void setUp() throws Exception {
    mine = VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 );
    versionMappings = new VersionMappings<Class<?>>( mine );
    other = VersionRange.from( 7, 0, 0 ).to( 7, 5, 9 );
  }

  @Test
  public void testIt() throws IOException {
    versionMappings.add( Object.class, other )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 15, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 2, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 5, 9 )
    ;

    versionMappings.add( String.class, other )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 1, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 15, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 2, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 5, 9 )
    ;

    versionMappings.add( Integer.class, other )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 1, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 1, 12 )
      .map( 1, 0, 2 ).to( 1, 15, 0 ).toDelegateVersion( 7, 0, 91 )
      .map( 2, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 5, 9 )
    ;

    assertEquals(
      "         -->   Integer    Object    String\n" +
        "------------------------------------------\n" +
        "   1.0.0 -->     7.1.1     7.0.1     7.1.1\n" +
        "   1.0.1 -->    7.1.12     7.0.2     7.0.2\n" +
        "   1.0.2 -->    7.0.91     7.1.0     7.1.0\n" +
        "  1.15.0 -->       |         |         |  \n" +
        "   2.0.0 -->     7.5.9     7.5.9     7.5.9\n" +
        "------------------------------------------\n",
      new VersionMappingsVisualizer<Class<?>>( versionMappings, new Comparator<Class<?>>() {
        @Override
        public int compare( Class<?> o1, Class<?> o2 ) {
          return o1.getName().compareTo( o2.getName() );
        }
      }, new ToString<Class<?>>() {
        @Nonnull
        @Override
        public String convert( @Nonnull Class<?> object ) {
          String[] parts = object.getName().split( "\\." );
          return parts[parts.length - 1];
        }
      }
      ).visualize() );
  }
}
