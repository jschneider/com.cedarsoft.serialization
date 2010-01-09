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
    private final Window.Serializer windowSerializer;
    private final Door.Serializer doorSerializer;
    private static final Version WINDOW_SERIALIZER_VERSION = new Version( 1, 0, 0 );
    private static final Version DOOR_SERIALIZER_VERSION = new Version( 1, 0, 0 );

    public Serializer( Window.Serializer windowSerializer, Door.Serializer doorSerializer ) {
      super( "room", "room", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
      this.windowSerializer = windowSerializer;
      this.doorSerializer = doorSerializer;

      verifyDelegatingSerializerVersion( windowSerializer, WINDOW_SERIALIZER_VERSION );
      verifyDelegatingSerializerVersion( doorSerializer, DOOR_SERIALIZER_VERSION );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Room object ) throws IOException, XMLStreamException {
      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );

      {
        SMOutputElement windowsElement = serializeTo.addElement( serializeTo.getNamespace(), "windows" );
        for ( Window window : object.getWindows() ) {
          windowSerializer.serialize( windowsElement.addElement( windowsElement.getNamespace(), "window" ), window );
        }
      }

      {
        SMOutputElement doorsElement = serializeTo.addElement( serializeTo.getNamespace(), "doors" );
        for ( Door door : object.getDoors() ) {
          doorSerializer.serialize( doorsElement.addElement( doorsElement.getNamespace(), "door" ), door );
        }
      }
    }

    @NotNull
    @Override
    public Room deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      String description = getChildText( deserializeFrom, "description" );

      final List<Window> windows = new ArrayList<Window>();
      nextTag( deserializeFrom, "windows" );
      visitChildren( deserializeFrom, new CB() {
        @Override
        public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
          windows.add( windowSerializer.deserialize( deserializeFrom, WINDOW_SERIALIZER_VERSION ) );
        }
      } );

      final List<Door> doors = new ArrayList<Door>();
      nextTag( deserializeFrom, "doors" );
      visitChildren( deserializeFrom, new CB() {
        @Override
        public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
          doors.add( doorSerializer.deserialize( deserializeFrom, DOOR_SERIALIZER_VERSION ) );
        }
      } );

      closeTag( deserializeFrom );

      return new Room( description, windows, doors );
    }
  }
}
