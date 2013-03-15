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

import com.cedarsoft.test.utils.AssertUtils;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.serialization.ui.DelegatesMappingVisualizer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class DelegatesTest {
  private House house;
  private Door door1;
  private Room hall;
  private Room kitchen;

  @Before
  public void setUp() throws Exception {
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
    assertEquals( new DelegatesMappingVisualizer( new MyRoomSerializer().getDelegatesMappings() ).visualize(),
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

    AssertUtils.assertXMLEquals(out.toString(),
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
                                  "</room>");

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
      add( new Door.Serializer() ).responsibleFor( Door.class )
        .map( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) ).toDelegateVersion( 1, 0, 0 );

      add( new Window.Serializer() ).responsibleFor( Window.class )
        .map( 1, 0, 0 ).to( 1, 5, 0 ).toDelegateVersion( 1, 0, 0 )
        .map( 2, 0, 0 ).toDelegateVersion( 2, 0, 0 )
      ;
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Room object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeToElementWithCharacters( "description", object.getDescription(), serializeTo );

      serializeCollectionToElement( object.getDoors(), Door.class, "doors", "door", serializeTo, formatVersion );
      serializeCollectionToElement( object.getWindows(), Window.class, "windows", "window", serializeTo, formatVersion );
    }

    @Nonnull
    @Override
    public Room deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull final Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      String description = getChildText( deserializeFrom, "description" );

      nextTag( deserializeFrom, "doors" );
      List<? extends Door> doors = deserializeCollection( deserializeFrom, Door.class, formatVersion );
      nextTag( deserializeFrom, "windows" );
      List<? extends Window> windows = deserializeCollection( deserializeFrom, Window.class, formatVersion );

      closeTag( deserializeFrom );
      return new Room( description, windows, doors );
    }
  }
}
