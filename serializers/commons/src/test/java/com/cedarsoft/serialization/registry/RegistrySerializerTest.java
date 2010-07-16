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

package com.cedarsoft.serialization.registry;

import com.cedarsoft.StillContainedException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.registry.DefaultRegistry;
import com.cedarsoft.registry.Registry;
import com.cedarsoft.registry.RegistryFactory;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 */
public class RegistrySerializerTest {
  private RegistrySerializer<String, Registry<String>> serializer;
  private InMemoryObjectsAccess access;

  @Before
  public void setup() {
    access = new InMemoryObjectsAccess();
    serializer = new RegistrySerializer<String, Registry<String>>( access, new AbstractStaxSerializer<String>( "text", "asdf", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @NotNull XMLStreamWriter serializeTo, @NotNull String object, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
        assert isVersionWritable( formatVersion );
        serializeTo.writeCharacters( object );
      }

      @NotNull
      @Override
      public String deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
        assert isVersionReadable( formatVersion );
        return getText( deserializeFrom );
      }

    }, new RegistrySerializer.IdResolver<String>() {
      @Override
      @NotNull
      public String getId( @NotNull String object ) {
        return object;
      }
    } );
  }

  @Test
  public void testDuplicates() throws IOException {
    Registry<String> registry = serializer.createConnectedRegistry( new MyRegistryFactory() );
    assertEquals( 0, registry.getStoredObjects().size() );
    registry.store( "asdf" );
    assertEquals( 1, registry.getStoredObjects().size() );
    try {
      registry.store( "asdf" );
      fail( "Where is the Exception" );
    } catch ( StillContainedException ignore ) {
    }
    assertEquals( 1, registry.getStoredObjects().size() );
  }

  @Test
  public void testDeserialize() throws IOException {
    serializer.serialize( "1" );

    assertEquals( 1, serializer.deserialize().size() );
    assertEquals( "1", serializer.deserialize().get( 0 ) );
  }

  @Test
  public void testConnected() throws IOException {
    serializer.serialize( "1" );

    Registry<String> registry = serializer.createConnectedRegistry( new MyRegistryFactory() );
    assertEquals( 1, registry.getStoredObjects().size() );
    assertEquals( "1", registry.getStoredObjects().get( 0 ) );

    registry.store( "2" );
    assertEquals( 2, registry.getStoredObjects().size() );

    assertEquals( 2, access.getIds().size() );
  }

  @Test
  public void testEmptyConstrucot() throws IOException {
    Registry<String> registry = new DefaultRegistry<String>( serializer.deserialize() );
    assertEquals( 0, registry.getStoredObjects().size() );
  }

  @Test
  public void testMulti() throws IOException {
    assertEquals( 0, access.getIds().size() );

    serializer.serialize( "1" );

    assertEquals( 1, access.getIds().size() );
    try {
      serializer.serialize( "1" );
      fail( "Where is the Exception" );
    } catch ( Exception e ) {
    }

    Set<? extends String> ids = access.getIds();
    assertEquals( 1, ids.size() );
    assertTrue( ids.contains( "1" ) );

    assertEquals( "1", serializer.getSerializer().deserialize( access.getInputStream( "1" ) ) );
  }

  private static class MyRegistryFactory implements RegistryFactory<String, Registry<String>> {
    @Override
    @NotNull
    public Registry<String> createRegistry( @NotNull List<? extends String> objects, @NotNull Comparator<String> comparator ) {
      return new DefaultRegistry<String>( objects, comparator );
    }
  }
}


