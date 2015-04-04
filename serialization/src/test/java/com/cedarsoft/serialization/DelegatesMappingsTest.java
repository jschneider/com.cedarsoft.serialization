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

import com.cedarsoft.version.UnsupportedVersionRangeException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionMismatchException;
import com.cedarsoft.version.VersionRange;
import org.junit.*;
import org.junit.rules.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 */
public class DelegatesMappingsTest {
  private final VersionRange mine = VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 );
  private VersionMappingTest.MySerializer serializer;
  private DelegatesMappings<Object, Object, IOException, OutputStream, InputStream> delegatesMappings;

  @Before
  public void setup() {
    serializer = new VersionMappingTest.MySerializer( VersionRange.from( 7, 0, 0 ).to( 7, 5, 9 ) );
    delegatesMappings = new DelegatesMappings<Object, Object, IOException, OutputStream, InputStream>( mine );
  }

  @Test
  public void testPrimitiveTypes() throws Exception {
    Class<Integer> primitiveType = int.class;

    Class<?> wrapper = ClassUtils.primitiveToWrapper( primitiveType );
    assertThat( wrapper ).isEqualTo( Integer.class );

    assertThat( primitiveType.isPrimitive() ).isTrue();
    Object cast = wrapper.cast( new Integer( 7 ) );

    assertThat( cast ).isNotNull();
    assertThat( cast ).isEqualTo( new Integer( 7 ) );

    try {
      primitiveType.cast( new Integer( 7 ) );
      fail("Where is the Exception");
    } catch ( ClassCastException ignore ) {
    }
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
      assertEquals( "Invalid mapping for <java.lang.Object>: Upper border of source range not mapped: Expected [2.0.0] but was [1.5.0]", e.getMessage() );
    }
  }

  @Test
  public void testVerify3() {
    {
      DelegatesMappings<Object, Object, IOException, OutputStream, InputStream> mappings = new DelegatesMappings<Object, Object, IOException, OutputStream, InputStream>( VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
      mappings.add( serializer ).responsibleFor( Object.class )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 0 );

      try {
        mappings.verify();
        fail( "Where is the Exception" );
      } catch ( VersionMismatchException e ) {
        assertEquals( "Invalid serialization/output version for <java.lang.Object>. Expected [7.5.9] but was [7.0.0]", e.getMessage() );
      }
    }

    {
      DelegatesMappings<Object, Object, IOException, OutputStream, InputStream> mappings = new DelegatesMappings<Object, Object, IOException, OutputStream, InputStream>( VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
      mappings.add( serializer ).responsibleFor( Object.class )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 5, 9 )
      ;

      mappings.verify();
    }
  }

  @Test
  public void testVerify2() throws Exception {
    delegatesMappings.add( serializer ).responsibleFor( String.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).toDelegateVersion( 7, 0, 3 )
      .map( 1, 0, 3 ).toDelegateVersion( 7, 0, 4 )
      .map( 1, 0, 4 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 1, 8, 4 ).to( 2, 0, 0 ).toDelegateVersion( 7, 1, 10 )
    ;

    assertEquals( delegatesMappings.getMapping( String.class ).getDelegateWriteVersion(), Version.valueOf( 7, 1, 10 ) );

    try {
      delegatesMappings.verify();
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException e ) {
      assertEquals( delegatesMappings.getMapping( String.class ).getDelegateWriteVersion(), Version.valueOf( 7, 1, 10 ) );
      assertEquals( "Invalid serialization/output version for <java.lang.String>. Expected [7.5.9] but was [7.1.10]", e.getMessage().trim() );
    }
  }

  @Test
  public void testVerify22() throws Exception {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 3 ).to( 2, 0, 0 ).toDelegateVersion( 7, 1, 1 )
    ;

    try {
      delegatesMappings.verify();
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException e ) {
      assertEquals( delegatesMappings.getMapping( Object.class ).getDelegateWriteVersion(), Version.valueOf( 7, 1, 1 ) );
      assertEquals( "Invalid serialization/output version for <java.lang.Object>. Expected [7.5.9] but was [7.1.1]", e.getMessage().trim() );
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

    expectedException.expect( IllegalArgumentException.class );
    expectedException.expectMessage( "An entry for the key <class java.lang.String> has still been added" );

    delegatesMappings.add( serializer ).responsibleFor( String.class );
  }

  @Test
  public void testErrorHandling() {
    expectedException.expect( UnsupportedVersionRangeException.class );
    expectedException.expectMessage( "The version range has still been mapped: Was <[1.0.0-1.0.0]>" );

    delegatesMappings.add( serializer ).responsibleFor( String.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 2 );
  }

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();


  public static class ClassUtils {
    @Nonnull
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP;

    static {
      PRIMITIVE_WRAPPER_MAP = new HashMap<Class<?>, Class<?>>();

      PRIMITIVE_WRAPPER_MAP.put( Boolean.TYPE, Boolean.class );
      PRIMITIVE_WRAPPER_MAP.put( Byte.TYPE, Byte.class );
      PRIMITIVE_WRAPPER_MAP.put( Character.TYPE, Character.class );
      PRIMITIVE_WRAPPER_MAP.put( Short.TYPE, Short.class );
      PRIMITIVE_WRAPPER_MAP.put( Integer.TYPE, Integer.class );
      PRIMITIVE_WRAPPER_MAP.put( Long.TYPE, Long.class );
      PRIMITIVE_WRAPPER_MAP.put( Double.TYPE, Double.class );
      PRIMITIVE_WRAPPER_MAP.put( Float.TYPE, Float.class );
    }

    private ClassUtils() {
    }

    @Nonnull
    public static Class<?> primitiveToWrapper( @Nonnull Class<?> primitiveType ) {
      @Nullable Class<?> wrapper = PRIMITIVE_WRAPPER_MAP.get( primitiveType );
      if ( wrapper == null ) {
        throw new IllegalStateException( "No wrapper found for <" + primitiveType + ">" );
      }
      return wrapper;
    }
  }
}
