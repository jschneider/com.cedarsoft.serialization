package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.serialization.ui.DelegatesMappingVisualizer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 *
 */
public class DelegatesTest {
  private House house;
  private Door door1;
  private Room hall;
  private Room kitchen;

  @BeforeMethod
  protected void setUp() throws Exception {
    house = new House( new Door( "Front door" ) );
    door1 = new Door( "door1" );
    hall = new Room( "hall" );
    kitchen = new Room( "kitchen" );


    hall.addWindow( new Window( "window1", 10, 11 ) );
    hall.addDoor( door1 );
    house.addRoom( hall );

    kitchen.addWindow( new Window( "window2", 20, 10 ) );
    kitchen.addWindow( new Window( "window3", 20, 10 ) );
    kitchen.addDoor( door1 );
    house.addRoom( kitchen );
  }

  @Test
  public void testVisualize() throws IOException {
    assertEquals( new DelegatesMappingVisualizer( new MyRoomSerializer().delegateMappings ).visualize(),
                  "         -->      Door    Window\n" +
                    "--------------------------------\n" +
                    "   1.0.0 -->     1.0.0     1.0.0\n" +
                    "   1.5.0 -->       |         |  \n" +
                    "   2.0.0 -->       |       2.0.0\n" +
                    "--------------------------------\n" );
  }

  @Test
  public void testSimple() throws IOException, SAXException {
    AbstractStaxMateSerializer<Room> roomSerializer = new MyRoomSerializer();

    //Now try to serialize them
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    roomSerializer.serialize( hall, out );

    AssertUtils.assertXMLEqual( out.toString(),
                                "<room xmlns=\"room/2.0.0\">\n" +
                                  "  <description>hall</description>\n" +
                                  "  <doors>\n" +
                                  "    <door>\n" +
                                  "      <description>door1</description>\n" +
                                  "    </door>\n" +
                                  "  </doors>\n" +
                                  "  <windows>\n" +
                                  "    <window width=\"10.0\" height=\"11.0\">\n" +
                                  "      <description>window1</description>\n" +
                                  "    </window>\n" +
                                  "  </windows>\n" +
                                  "</room>" );

    assertEquals( roomSerializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), hall );

    //Deserialize an old one!
    assertEquals( roomSerializer.deserialize( new ByteArrayInputStream( ( "<room xmlns=\"room/1.0.0\">\n" +
      "  <description>hall</description>\n" +
      "  <doors>\n" +
      "    <door>\n" +
      "      <description>door1</description>\n" +
      "    </door>\n" +
      "  </doors>\n" +
      "  <windows>\n" +
      "    <window>" +
      "       <width>10.0</width>" +
      "       <height>11.0</height>" +
      "      <description>window1</description>\n" +
      "    </window>\n" +
      "  </windows>\n" +
      "</room>" ).getBytes() ) ), hall );
  }

  private static class MyRoomSerializer extends AbstractStaxMateSerializer<Room> {
    private MyRoomSerializer() {
      super( "room", "room", VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );
    }

    {
      delegateMappings.add( new Door.Serializer() ).responsibleFor( Door.class )
        .map( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) ).toDelegateVersion( 1, 0, 0 );

      delegateMappings.add( new Window.Serializer() ).responsibleFor( Window.class )
        .map( 1, 0, 0 ).to( 1, 5, 0 ).toDelegateVersion( 1, 0, 0 )
        .map( 2, 0, 0 ).toDelegateVersion( 2, 0, 0 )
        ;
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Room object ) throws IOException, XMLStreamException {
      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );

      SMOutputElement doorsElement = serializeTo.addElement( serializeTo.getNamespace(), "doors" );
      for ( Door door : object.getDoors() ) {
        SMOutputElement doorElement = doorsElement.addElement( doorsElement.getNamespace(), "door" );
        delegateMappings.getSerializer( Door.class ).serialize( doorElement, door );
      }

      SMOutputElement windowsElement = serializeTo.addElement( serializeTo.getNamespace(), "windows" );
      for ( Window window : object.getWindows() ) {
        SMOutputElement windowElement = windowsElement.addElement( windowsElement.getNamespace(), "window" );
        delegateMappings.getSerializer( Window.class ).serialize( windowElement, window );
      }
    }

    @NotNull
    @Override
    public Room deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull final Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      String description = getChildText( deserializeFrom, "description" );

      final List<Door> doors = new ArrayList<Door>();

      nextTag( deserializeFrom, "doors" );
      visitChildren( deserializeFrom, new CB() {
        @Override
        public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
          doors.add( delegateMappings.deserialize( Door.class, formatVersion, deserializeFrom ) );
        }
      } );


      final List<Window> windows = new ArrayList<Window>();
      nextTag( deserializeFrom, "windows" );

      visitChildren( deserializeFrom, new CB() {
        @Override
        public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
          windows.add( delegateMappings.deserialize( Window.class, formatVersion, deserializeFrom ) );
        }
      } );

      closeTag( deserializeFrom );
      return new Room( description, windows, doors );
    }
  }
}
