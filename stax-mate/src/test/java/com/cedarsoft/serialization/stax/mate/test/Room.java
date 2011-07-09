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

package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.serialization.stax.CollectionsMapping;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Room {
  @Nonnull

  private final String description;

  @Nonnull
  private final List<Window> windows = new ArrayList<Window>();

  @Nonnull
  private final List<Door> doors = new ArrayList<Door>();

  public Room( @Nonnull String description ) {
    this.description = description;
  }

  public Room( @Nonnull String description, Collection<? extends Window> windows, Collection<? extends Door> doors ) {
    this.description = description;
    this.windows.addAll( windows );
    this.doors.addAll( doors );
  }

  public void addDoor( @Nonnull Door door ) {
    this.doors.add( door );
  }

  public void addWindow( @Nonnull Window window ) {
    this.windows.add( window );
  }

  @Nonnull
  public String getDescription() {
    return description;
  }

  @Nonnull
  public List<? extends Window> getWindows() {
    return Collections.unmodifiableList( windows );
  }

  @Nonnull
  public List<? extends Door> getDoors() {
    return Collections.unmodifiableList( doors );
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Room ) ) return false;

    Room room = ( Room ) o;

    if ( !description.equals( room.description ) ) return false;
    if ( !doors.equals( room.doors ) ) return false;
    if ( !windows.equals( room.windows ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = description.hashCode();
    result = 31 * result + windows.hashCode();
    result = 31 * result + doors.hashCode();
    return result;
  }

  public static class Serializer extends AbstractStaxMateSerializer<Room> {

    public Serializer( Window.Serializer windowSerializer, Door.Serializer doorSerializer ) {
      super( "room", "room", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );

      add( windowSerializer ).responsibleFor( Window.class )
        .map( 1, 0, 0 ).toDelegateVersion( 2, 0, 0 )
      ;
      add( doorSerializer ).responsibleFor( Door.class )
        .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
      ;

      getDelegatesMappings().verify();
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Room object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );

      serializeCollection( object.getWindows(), Window.class, "window", serializeTo, formatVersion );
      serializeCollection( object.getDoors(), Door.class, "door", serializeTo, formatVersion );
    }

    @Nonnull
    @Override
    public Room deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      String description = getChildText( deserializeFrom, "description" );

      List<Window> windows = new ArrayList<Window>();
      List<Door> doors = new ArrayList<Door>();
      deserializeCollections( deserializeFrom, formatVersion, new CollectionsMapping()
        .append( Window.class, windows, "window" )
        .append( Door.class, doors, "door" )
      );

      return new Room( description, windows, doors );
    }
  }
}
