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

package com.cedarsoft.serialization;

import com.cedarsoft.UnsupportedVersionRangeException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class DelegatesMappingsTest {
  private final VersionRange mine = VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 );
  private  DelegateMappingTest.MySerializer serializer ;
  private DelegatesMappings<Object, Object, IOException> delegatesMappings;

  @BeforeMethod
  public void setup() {
    serializer = new DelegateMappingTest.MySerializer( VersionRange.from( 7, 0, 0 ).to( 7, 5, 9 ) );
    delegatesMappings = new DelegatesMappings<Object, Object, IOException>( mine );
  }

  @Test
  public void testVerify() throws Exception {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      ;

    try {
      delegatesMappings.verify();
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException e ) {
      assertEquals( e.getMessage(), "Invalid mapping for <java.lang.Object>: Upper border of source range not mapped: Expected <2.0.0> but was <1.5.0>" );
    }
  }

  @Test
  public void testVerify3() {
    {
      DelegatesMappings<Object, Object, IOException> mappings = new DelegatesMappings<Object, Object, IOException>( VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
      mappings.add( serializer ).responsibleFor( Object.class )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 0 );

      try {
        mappings.verify();
        fail( "Where is the Exception" );
      } catch ( VersionMismatchException e ) {
        assertEquals( e.getMessage(), "Invalid serialization/output version for <java.lang.Object>. Expected <7.0.0> but was <7.5.9>" );
      }
    }

    {
      DelegatesMappings<Object, Object, IOException> mappings = new DelegatesMappings<Object, Object, IOException>( VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
      mappings.add( serializer ).responsibleFor( Object.class )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 5, 9 )
        ;

      mappings.verify();
    }
  }

  @Test
  public void testVerify2() throws Exception {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 3 ).to( 2, 0, 0 ).toDelegateVersion( 7, 1, 0 )
      ;

    delegatesMappings.add( serializer ).responsibleFor( String.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).toDelegateVersion( 7, 0, 3 )
      .map( 1, 0, 3 ).toDelegateVersion( 7, 0, 4 )
      .map( 1, 0, 4 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 1, 8, 4 ).to( 2, 0, 0 ).toDelegateVersion( 7, 1, 10 )
      ;

    try {
      delegatesMappings.verify();
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException e ) {
      assertEquals( e.getMessage(), "Invalid serialization/output version for <java.lang.String>. Expected <7.5.9> but was <7.1.10>" );
    }
  }

  @Test
  public void testIt() {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      ;

    //    delegatesMappings.addMappingFor( serializer )
    //      .map( VersionRange.from( 1, 0, 0 ).single(), new Version( 7, 0, 1 ) )
    //      .map( VersionRange.from( 1, 0, 1 ).single(), new Version( 7, 0, 2 ) )
    //      .map( VersionRange.from( 1, 0, 2 ).to( 1, 5, 0 ), new Version( 7, 1, 0 ) );
    //

    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 0 ) ), new Version( 7, 0, 1 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 1 ) ), new Version( 7, 0, 2 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 2 ) ), new Version( 7, 1, 0 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 3 ) ), new Version( 7, 1, 0 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 5, 0 ) ), new Version( 7, 1, 0 ) );

    assertSame( delegatesMappings.getSerializer( Object.class ), serializer );
  }

  @Test
  public void testDuplicate() {
    delegatesMappings.add( serializer ).responsibleFor( String.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 2 );

    try {
      delegatesMappings.add( serializer ).responsibleFor( String.class );
      fail( "Where is the Exception" );
    } catch ( IllegalArgumentException e ) {
      assertEquals( e.getMessage(), "A serializer for the key <class java.lang.String> has still been added" );
    }
  }

  @Test
  public void testErrorHandling() {
    try {
      delegatesMappings.add( serializer ).responsibleFor( String.class )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 2 );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionRangeException e ) {
      assertEquals( e.getMessage(), "The version range has still been mapped: Was <[1.0.0-1.0.0]>" );
    }
  }
}
