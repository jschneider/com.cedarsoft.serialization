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

import com.cedarsoft.version.UnsupportedVersionException;
import com.cedarsoft.version.UnsupportedVersionRangeException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionMismatchException;
import com.cedarsoft.version.VersionRange;
import org.junit.*;
import org.junit.rules.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


/**
 *
 */
public class VersionMappingsTest {
  private final VersionRange mine = VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 );
  private VersionMappings<Class<?>> mapping;

  @Before
  public void setup() {
    mapping = new VersionMappings<Class<?>>( mine );
  }

  @Test
  public void testValidate() {
    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 6, 0 ).to( 1, 9, 0 ).toDelegateVersion( 8, 0, 0 )
    ;

    expectedException.expect( VersionMismatchException.class );
    expectedException.expectMessage( "Invalid mapping for <class java.lang.String>: Upper border of source range not mapped: Expected [2.0.0] but was [1.9.0]" );

    mapping.verify();
  }

  @Test
  public void testValidate2() {
    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 6, 0 ).to( 2, 0, 0 ).toDelegateVersion( 8, 0, 0 )
    ;

    expectedException.expect( VersionMismatchException.class );
    expectedException.expectMessage( "Invalid mapping for <class java.lang.String>: Lower border of source range not mapped: Expected [1.0.0] but was [1.0.1]" );

    mapping.verify();
  }

  @Test
  public void testBasicMapping() {
    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 1, 6, 0 ).to( 2, 0, 0 ).toDelegateVersion( 8, 0, 0 )
    ;

    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 0, 0 ) ), is( Version.valueOf( 7, 0, 1 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 0, 1 ) ), is( Version.valueOf( 7, 0, 2 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 0, 2 ) ), is( Version.valueOf( 7, 1, 0 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 0, 3 ) ), is( Version.valueOf( 7, 1, 0 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 0, 4 ) ), is( Version.valueOf( 7, 1, 0 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 5, 0 ) ), is( Version.valueOf( 7, 1, 0 ) ) );


    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 6, 0 ) ), is( Version.valueOf( 8, 0, 0 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 6, 1 ) ), is( Version.valueOf( 8, 0, 0 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 99, 99 ) ), is( Version.valueOf( 8, 0, 0 ) ) );
    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 2, 0, 0 ) ), is( Version.valueOf( 8, 0, 0 ) ) );

    expectedException.expect( UnsupportedVersionException.class );
    expectedException.expectMessage( "No delegate version mapped for source version <1.5.1>" );

    assertThat( mapping.resolveVersion( String.class, Version.valueOf( 1, 5, 1 ) ), is( Version.valueOf( 7, 1, 0 ) ) );
  }

  @Test
  public void testDuplicate() {
    expectedException.expect( UnsupportedVersionRangeException.class );
    expectedException.expectMessage( "The version range has still been mapped: Was <[1.0.0-1.0.0]>" );

    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 2 )
    ;
  }

  @Test
  public void testDuplicate2() {
    expectedException.expect( UnsupportedVersionRangeException.class );
    expectedException.expectMessage( "The version range has still been mapped: Was <[1.0.0-2.0.0]>" );

    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 0, 2 )
    ;
  }

  @Test
  public void testDuplicate3() {
    expectedException.expect( UnsupportedVersionRangeException.class );
    expectedException.expectMessage( "The version range has still been mapped: Was <[1.0.0-2.0.0]>" );

    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 0, 2 )
    ;
  }

  @Test
  public void testDuplicate4() {
    expectedException.expect( UnsupportedVersionRangeException.class );
    expectedException.expectMessage( "The version range has still been mapped: Was <[1.0.0-2.0.0]>" );

    mapping.add( String.class, VersionRange.from( 7, 0, 0 ).to( 8, 0, 0 ) )
      .map( 2, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 0, 2 )
    ;
  }

  @Test
  public void testDuplicate1() {
    mapping.addMapping( String.class, VersionRange.from( 7, 0, 0 ).to() );

    expectedException.expect( IllegalArgumentException.class );
    mapping.addMapping( String.class, VersionRange.from( 7, 0, 0 ).to() );
  }

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

}
