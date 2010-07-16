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

package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

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
  @NotNull
  @NonNls
  private final String description;

  @NotNull
  private final List<Window> windows = new ArrayList<Window>();

  @NotNull
  private final List<Door> doors = new ArrayList<Door>();

  public Room( @NotNull String description ) {
    this.description = description;
  }

  public Room( @NotNull String description, Collection<? extends Window> windows, Collection<? extends Door> doors ) {
    this.description = description;
    this.windows.addAll( windows );
    this.doors.addAll( doors );
  }

  public void addDoor( @NotNull Door door ) {
    this.doors.add( door );
  }

  public void addWindow( @NotNull Window window ) {
    this.windows.add( window );
  }

  @NotNull
  public String getDescription() {
    return description;
  }

  @NotNull
  public List<? extends Window> getWindows() {
    return Collections.unmodifiableList( windows );
  }

  @NotNull
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
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Room object, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );

      serializeCollectionToElement( object.getWindows(), Window.class, "windows", "window", serializeTo, formatVersion );
      serializeCollectionToElement( object.getDoors(), Door.class, "doors", "door", serializeTo, formatVersion );
    }

    @NotNull
    @Override
    public Room deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      String description = getChildText( deserializeFrom, "description" );

      nextTag( deserializeFrom, "windows" );
      final List<? extends Window> windows = deserializeCollection( deserializeFrom, Window.class, formatVersion );

      nextTag( deserializeFrom, "doors" );
      final List<? extends Door> doors = deserializeCollection( deserializeFrom, Door.class, formatVersion );

      closeTag( deserializeFrom );
      return new Room( description, windows, doors );
    }
  }
}
