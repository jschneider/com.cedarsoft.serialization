package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class RoomSerializerTest extends AbstractXmlSerializerTest<Room> {
  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return
      "<room>\n" +
        "  <description>descr</description>\n" +
        "  <windows>\n" +
        "    <window width=\"20.0\" height=\"30.0\">\n" +
        "      <description>asdf</description>\n" +
        "    </window>\n" +
        "    <window width=\"50.0\" height=\"60.7\">\n" +
        "      <description>asdf2</description>\n" +
        "    </window>\n" +
        "  </windows>\n" +
        "  <doors>\n" +
        "    <door>\n" +
        "      <description>asdf</description>\n" +
        "    </door>\n" +
        "    <door>\n" +
        "      <description>asdf2</description>\n" +
        "    </door>\n" +
        "    <door>\n" +
        "      <description>asdf3</description>\n" +
        "    </door>\n" +
        "  </doors>\n" +
        "</room>";
  }

  @NotNull
  @Override
  protected Serializer<Room> getSerializer() throws Exception {
    return new Room.Serializer( new Window.Serializer(), new Door.Serializer() );
  }

  @NotNull
  @Override
  protected Room createObjectToSerialize() throws Exception {
    List<Window> windows = Arrays.asList( new Window( "asdf", 20, 30 ), new Window( "asdf2", 50, 60.7 ) );
    List<Door> doors = Arrays.asList( new Door( "asdf" ), new Door( "asdf2" ), new Door( "asdf3" ) );
    return new Room( "descr", windows, doors );
  }
}
