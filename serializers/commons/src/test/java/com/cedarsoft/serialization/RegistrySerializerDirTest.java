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

import com.cedarsoft.StillContainedException;
import com.cedarsoft.TestUtils;
import com.cedarsoft.registry.DefaultRegistry;
import com.cedarsoft.registry.Registry;
import com.cedarsoft.registry.RegistryFactory;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;


/**
 *
 */
public class RegistrySerializerDirTest {
  private RegistrySerializer<String, Registry<String>> serializer;
  private SerializedObjectsAccess access;
  private File baseDir;

  @BeforeMethod
  public void setup() {
    baseDir = TestUtils.createEmptyTmpDir();

    access = new DirBasedSerializedObjectsAccess( baseDir );
    serializer = new RegistrySerializer<String, Registry<String>>( access, new RegistrySerializingStrategy<String>() {
      @NotNull
      @Override
      public String deserialize( @NotNull @NonNls String id, @NotNull SerializedObjectsAccess serializedObjectsAccess ) throws IOException {
        File dir = ( ( DirBasedSerializedObjectsAccess ) serializedObjectsAccess ).getDirectory( id );
        return FileUtils.readFileToString( new File( dir, "data" ) );
      }

      @Override
      public void serialize( @NotNull String object, @NotNull @NonNls String id, @NotNull SerializedObjectsAccess serializedObjectsAccess ) throws IOException {
        File dir = ( ( DirBasedSerializedObjectsAccess ) serializedObjectsAccess ).addDirectory( id );
        File data = new File( dir, "data" );
        FileUtils.writeStringToFile( data, object );
      }

    }, new RegistrySerializer.IdResolver<String>() {
      @Override
      @NotNull
      public String getId( @NotNull String object ) {
        return object;
      }
    } );
  }

  @AfterMethod
  protected void tearDown() throws Exception {
    FileUtils.deleteDirectory( baseDir );
  }

  @Test
  public void testDuplicates() throws IOException {
    Registry<String> registry = serializer.createConnectedRegistry( new MyRegistryFactory() );
    assertEquals( registry.getStoredObjects().size(), 0 );
    registry.store( "asdf" );
    assertEquals( registry.getStoredObjects().size(), 1 );
    try {
      registry.store( "asdf" );
      fail( "Where is the Exception" );
    } catch ( StillContainedException ignore ) {
    }
    assertEquals( registry.getStoredObjects().size(), 1 );
  }

  @Test
  public void testDeserialize() throws IOException {
    serializer.serialize( "1" );

    assertEquals( serializer.deserialize().size(), 1 );
    assertEquals( serializer.deserialize().get( 0 ), "1" );
  }

  @Test
  public void testConnected() throws IOException {
    serializer.serialize( "1" );

    Registry<String> registry = serializer.createConnectedRegistry( new MyRegistryFactory() );
    assertEquals( registry.getStoredObjects().size(), 1 );
    assertEquals( registry.getStoredObjects().get( 0 ), "1" );

    registry.store( "2" );
    assertEquals( registry.getStoredObjects().size(), 2 );

    assertEquals( access.getStoredIds().size(), 2 );
  }

  @Test
  public void testEmptyConstrucot() throws IOException {
    Registry<String> registry = new DefaultRegistry<String>( serializer.deserialize() );
    assertEquals( registry.getStoredObjects().size(), 0 );
  }

  @Test
  public void testMulti() throws IOException {
    assertEquals( access.getStoredIds().size(), 0 );

    serializer.serialize( "1" );

    assertEquals( access.getStoredIds().size(), 1 );
    try {
      serializer.serialize( "1" );
      fail( "Where is the Exception" );
    } catch ( Exception e ) {
    }

    Set<? extends String> ids = access.getStoredIds();
    assertEquals( ids.size(), 1 );
    assertTrue( ids.contains( "1" ) );
  }

  private static class MyRegistryFactory implements RegistryFactory<String, Registry<String>> {
    @Override
    @NotNull
    public Registry<String> createRegistry( @NotNull List<? extends String> objects, @NotNull Comparator<String> comparator ) {
      return new DefaultRegistry<String>( objects, comparator );
    }
  }
}