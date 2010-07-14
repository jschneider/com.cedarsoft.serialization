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

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.UnsupportedVersionRangeException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;


/**
 *
 */
public class DelegateMappingTest {
  @Test
  public void testVerify() {
    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ), delegate.getFormatVersionRange() );

    try {
      mapping.verify();
      fail( "Where is the Exception" );
    } catch ( VersionException e ) {
      assertEquals( "No mappings available", e.getMessage() );
    }

    mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );

    try {
      mapping.verify();
      fail( "Where is the Exception" );
    } catch ( VersionException e ) {
      assertEquals( "Upper border of source range not mapped: Expected <2.2.7> but was <0.5.0>", e.getMessage() );
    }

    mapping.addMapping( new VersionRange( new Version( 0, 5, 1 ), new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
    mapping.addMapping( new VersionRange( new Version( 1, 0, 1 ), new Version( 2, 2, 7 ) ), new Version( 2, 0, 0 ) );

    mapping.verify();
  }

  @Test
  public void testWriteVersion() {
    VersionRange myVersionRange = new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 1 ) );
    VersionRange delegateVersionRange = new VersionRange( new Version( 2, 0, 0 ), new Version( 2, 0, 1 ) );

    DelegateMapping mapping = new DelegateMapping( myVersionRange, new MySerializer( delegateVersionRange ).getFormatVersionRange() );

    mapping.map( 1, 0, 0 ).to( 1, 0, 1 ).toDelegateVersion( 2, 0, 0 );
    assertEquals( mapping.getDelegateWriteVersion(), Version.valueOf( 2, 0, 0 ) );
  }

  @Test
  public void testWriteVersion2() {
    VersionRange myVersionRange = new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 1 ) );
    VersionRange delegateVersionRange = new VersionRange( new Version( 2, 0, 0 ), new Version( 2, 0, 1 ) );

    DelegateMapping mapping = new DelegateMapping( myVersionRange, new MySerializer( delegateVersionRange ).getFormatVersionRange() );

    mapping.map( 1, 0, 0 ).to( 1, 0, 0 ).toDelegateVersion( 2, 0, 0 );
    assertEquals( mapping.getDelegateWriteVersion(), Version.valueOf( 2, 0, 0 ) );

    try {
      mapping.verify();
      fail( "Where is the Exception" );
    } catch ( VersionException e ) {
      assertEquals( "Upper border of source range not mapped: Expected <1.0.1> but was <1.0.0>", e.getMessage() );
    }

    mapping.map( 1, 0, 1 ).to( 1, 0, 1 ).toDelegateVersion( 2, 0, 1 );
    assertEquals( mapping.getDelegateWriteVersion(), Version.valueOf( 2, 0, 1 ) );
  }

  @Test
  public void testBasic() {
    MySerializer delegate = new MySerializer( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );
    DelegateMapping mapping = new DelegateMapping( VersionRange.from( 0, 5, 0 ).to( 2, 2, 7 ), delegate.getFormatVersionRange() );


    //Version 0.5.0 of me --> 1.0.0
    mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
    //Version 0.5.1 - 1.0.0 of me --> 1.0.1
    mapping.addMapping( new VersionRange( new Version( 0, 5, 1 ), new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
    //Version 1.0.1 - 2.2.7 of me --> 2.0.0
    mapping.addMapping( new VersionRange( new Version( 1, 0, 1 ), new Version( 2, 2, 7 ) ), new Version( 2, 0, 0 ) );


    assertEquals( mapping.resolveVersion( new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
    assertEquals( mapping.resolveVersion( new Version( 0, 5, 1 ) ), new Version( 1, 0, 1 ) );
    assertEquals( mapping.resolveVersion( new Version( 0, 9, 1 ) ), new Version( 1, 0, 1 ) );
    assertEquals( mapping.resolveVersion( new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
    assertEquals( mapping.resolveVersion( new Version( 1, 0, 1 ) ), new Version( 2, 0, 0 ) );
  }

  @Test
  public void testDuplicate() {
    DelegateMapping mapping = new DelegateMapping( VersionRange.from( 0, 5, 0 ).to( 2, 2, 7 ), VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );


    //Version 0.5.0 of me --> 1.0.0
    mapping.addMapping( VersionRange.from( 0, 5, 0 ).to(), new Version( 1, 0, 0 ) );
    try {
      mapping.addMapping( VersionRange.from( 0, 5, 0 ).to(), new Version( 1, 0, 0 ) );
      fail( "Where is the Exception" );
    } catch ( VersionException e ) {
      assertEquals( "The version range has still been mapped: Was <[0.5.0-0.5.0]>", e.getMessage() );
    }
  }

  @Test
  public void testDelegateWrongVersion() {
    DelegateMapping mapping = new DelegateMapping( VersionRange.from( 0, 5, 0 ).to( 2, 2, 7 ), VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );
    MySerializer delegate = new MySerializer( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );

    try {
      mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 0, 0, 1 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionException e ) {
      assertEquals( e.getActual(), new Version( 0, 0, 1 ) );
      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
      assertEquals( "Invalid delegate version: Was <0.0.1>. Supported range <[1.0.0-2.0.0]>", e.getMessage() );
    }

    try {
      mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 3, 0, 1 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionException e ) {
      assertEquals( e.getActual(), new Version( 3, 0, 1 ) );
      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
      assertEquals( "Invalid delegate version: Was <3.0.1>. Supported range <[1.0.0-2.0.0]>", e.getMessage() );
    }
  }

  @Test
  public void testMyWrongVersion() {
    DelegateMapping mapping = new DelegateMapping( VersionRange.from( 0, 5, 0 ).to( 2, 2, 7 ), VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );

    try {
      mapping.addMapping( VersionRange.from( 0, 4, 0 ).to( 0, 5, 0 ), new Version( 1, 0, 0 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionRangeException e ) {
      assertEquals( e.getActual(), new VersionRange( new Version( 0, 4, 0 ), new Version( 0, 5, 0 ) ) );
      assertEquals( e.getSupportedRange(), mapping.getSourceVersionRange() );
      assertEquals( "Invalid source range: Was <[0.4.0-0.5.0]> but expected <[0.5.0-2.2.7]>", e.getMessage() );
    }

    try {
      mapping.addMapping( VersionRange.from( 1, 0, 0 ).to( 2, 2, 8 ), new Version( 1, 0, 0 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionRangeException e ) {
      assertEquals( e.getActual(), new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 2, 8 ) ) );
      assertEquals( e.getSupportedRange(), mapping.getSourceVersionRange() );
      assertEquals( "Invalid source range: Was <[1.0.0-2.2.8]> but expected <[0.5.0-2.2.7]>", e.getMessage() );
    }
  }

  //  @Test
  //  public void testBasic() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    //Version 0.5.0 of me --> 1.0.0
  //    mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //    //Version 0.5.1 - 1.0.0 of me --> 1.0.1
  //    mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 1 ), new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
  //    //Version 1.0.1 - 2.2.7 of me --> 2.0.0
  //    mapping.addMapping( delegate, new VersionRange( new Version( 1, 0, 1 ), new Version( 2, 2, 7 ) ), new Version( 2, 0, 0 ) );
  //
  //
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 0, 5, 1 ) ), new Version( 1, 0, 1 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 0, 9, 1 ) ), new Version( 1, 0, 1 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 1, 0, 1 ) ), new Version( 2, 0, 0 ) );
  //  }
  //
  //  @Test
  //  public void testDuplicate() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    //Version 0.5.0 of me --> 1.0.0
  //    mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //      fail("Where is the Exception");
  //    } catch ( IllegalArgumentException ignore ) {
  //    }
  //
  //  }
  //
  //  @Test
  //  public void testDelegateWrongVersion() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 0, 0, 1 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 0, 0, 1 ) );
  //      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
  //    }
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 3, 0, 1 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 3, 0, 1 ) );
  //      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
  //    }
  //  }
  //
  //  @Test
  //  public void testMyWrongVersion() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 4, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 0, 0, 1 ) );
  //      assertEquals( e.getSupportedRange(), mapping.getSourceVersionRange() );
  //    }
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 2, 8 ) ), new Version( 1, 0, 0 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 2, 2, 8 ) );
  //      assertEquals( e.getSupportedRange(), mapping.getSourceVersionRange() );
  //    }
  //  }
  //

  public static class MySerializer extends AbstractSerializer<Object, Object, Object, IOException> {
    public MySerializer( @NotNull VersionRange formatVersionRange ) {
      super( formatVersionRange );
    }

    @Override
    public void serialize( @NotNull Object serializeTo, @NotNull Object object, SerializationContext context ) throws IOException, IOException {
    }

    @NotNull
    @Override
    public Object deserialize( @NotNull Object deserializeFrom, @NotNull Version formatVersion, DeserializationContext context ) throws IOException, VersionException, IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void serialize( @NotNull Object object, @NotNull OutputStream out ) throws IOException {
    }

    @NotNull
    @Override
    public Object deserialize( @NotNull InputStream in ) throws IOException, VersionException {
      throw new UnsupportedOperationException();
    }
  }
}
