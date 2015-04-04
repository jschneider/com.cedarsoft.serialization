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

import com.cedarsoft.test.utils.MockitoTemplate;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import org.junit.*;
import org.junit.rules.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class SerializingStrategySupportTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private SerializingStrategySupport<Integer, StringBuilder, String, IOException, OutputStream, InputStream> support;

  @Before
  public void setUp() throws Exception {
    support = new SerializingStrategySupport<Integer, StringBuilder, String, IOException, OutputStream, InputStream>( VersionRange.single(1, 0, 0) );
  }

  @Test
  public void testIt() throws Exception {
    assertEquals( 0, support.getStrategies().size() );

    expectedException.expect( NotFoundException.class );
    expectedException.expectMessage( "No strategy found for id <daId>" );
    support.findStrategy( "daId" );
  }

  @Test
  public void festFind() throws Exception {
    assertEquals( 0, support.getStrategies().size() );

    expectedException.expect( NotFoundException.class );
    expectedException.expectMessage( "No strategy found for object <77>" );
    support.findStrategy( 77 );
  }

  @Test
  public void testVerify() throws Exception {
    expectedException.expect( SerializationException.class );
    expectedException.expectMessage( "No strategies available. Verification not possible." );
    assertFalse( support.verify() );
  }

  @Test
  public void testVersionMappings() throws Exception {
    new MockitoTemplate() {
      @Mock
      private SerializingStrategy<Integer, StringBuilder, String, IOException, OutputStream, InputStream> strategy1;
      @Mock
      private SerializingStrategy<Integer, StringBuilder, String, IOException, OutputStream, InputStream> strategy2;

      @Override
      protected void stub() throws Exception {
        when( strategy1.getFormatVersionRange() ).thenReturn( VersionRange.single( 0, 0, 1 ) );
        when( strategy2.getFormatVersionRange() ).thenReturn( VersionRange.single( 0, 0, 2 ) );

        when( strategy1.getId() ).thenReturn( "id1" );
        when( strategy2.getId() ).thenReturn( "id2" );
      }

      @Override
      protected void execute() throws Exception {
        support.addStrategy( strategy1 ).map( 1, 0, 0 ).toDelegateVersion( 0, 0, 1 );
        support.addStrategy( strategy2 ).map( 1, 0, 0 ).toDelegateVersion( 0, 0, 2 );
        assertTrue( support.verify() );

        assertEquals( 1, support.getVersionMappings().getMappedVersions().size() );
        assertEquals( Version.valueOf(0, 0, 1), support.resolveVersion( strategy1, Version.valueOf( 1, 0, 0 ) ) );
        assertEquals( Version.valueOf( 0, 0, 2 ), support.resolveVersion( strategy2, Version.valueOf( 1, 0, 0 ) ) );
      }

      @Override
      protected void verifyMocks() throws Exception {
        Mockito.verify( strategy1 ).getFormatVersionRange();
        Mockito.verifyNoMoreInteractions( strategy1 );

        Mockito.verify( strategy2 ).getFormatVersionRange();
        Mockito.verifyNoMoreInteractions( strategy2 );
      }
    }.run();
  }

  @Test
  public void festFind3() throws Exception {
    new MockitoTemplate() {
      @Mock
      private SerializingStrategy<Integer, StringBuilder, String, IOException, OutputStream, InputStream> strategy;

      @Override
      protected void stub() throws Exception {
        when( strategy.getFormatVersionRange() ).thenReturn( VersionRange.single( 1, 0, 0 ) );
        when( strategy.getId() ).thenReturn( "daId" );
        when( strategy.supports( 77 ) ).thenReturn( true );
        when( strategy.supports( 99 ) ).thenReturn( false );
      }

      @Override
      protected void execute() throws Exception {
        assertNotNull( strategy );

        assertEquals( 0, support.getStrategies().size() );
        support.addStrategy( strategy );
        assertEquals( 1, support.getStrategies().size() );

        assertNotNull( support.findStrategy( "daId" ) );

        try {
          support.findStrategy( "otherId" );
          fail( "Where is the Exception" );
        } catch ( NotFoundException ignore ) {
        }

        assertNotNull( support.findStrategy( 77 ) );

        try {
          support.findStrategy( 99 );
          fail( "Where is the Exception" );
        } catch ( NotFoundException ignore ) {
        }
      }

      @Override
      protected void verifyMocks() throws Exception {
        verify( strategy ).getFormatVersionRange();
        verify( strategy, times( 2 ) ).getId();
        verify( strategy ).supports( 77 );
        verify( strategy ).supports( 99 );
        verifyNoMoreInteractions( strategy );
      }
    }.run();
  }
}
