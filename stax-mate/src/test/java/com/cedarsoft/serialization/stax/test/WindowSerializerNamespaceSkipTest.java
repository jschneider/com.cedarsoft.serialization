package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class WindowSerializerNamespaceSkipTest {
  @NotNull
  protected Serializer<Window> getSerializer() throws Exception {
    return new DaSkippingSerializer();
  }

  @Test
  public void testIt() throws IOException {
    DaSkippingSerializer serializer = new DaSkippingSerializer();
    Window deserialized = serializer.deserialize( new ByteArrayInputStream( (
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<window xmlns=\"window/0.9.0\" width=\"123.3\" height=\"444.4\">\n" +
        "  <other xmlns=\"window/2.0.0\"><a><b>content of b</b></a></other>\n" +
        "  <description>the window</description>\n" +
        "</window>" ).getBytes() ) );

    assertEquals( deserialized.getDescription(), "the window" );
    assertEquals( deserialized.getHeight(), 444.4 );
    assertEquals( deserialized.getWidth(), 123.3 );
  }

  public static class DaSkippingSerializer extends AbstractStaxMateSerializer<Window> {
    public DaSkippingSerializer() {
      super( "window", "window", new VersionRange( new Version( 0, 9, 0 ), new Version( 0, 9, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Window object ) throws IOException, XMLStreamException {
      serializeTo.addAttribute( "width", String.valueOf( object.getWidth() ) );
      serializeTo.addAttribute( "height", String.valueOf( object.getHeight() ) );

      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );
    }

    @NotNull
    @Override
    public Window deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      double width = Double.parseDouble( deserializeFrom.getAttributeValue( null, "width" ) );
      double height = Double.parseDouble( deserializeFrom.getAttributeValue( null, "height" ) );

      //Skip it
//      nextTag( deserializeFrom, "other" );
//      skipCurrentTag( deserializeFrom );

      //yeah
      nextTag( deserializeFrom, "description", getNameSpaceUri() );
      String description = getText( deserializeFrom );
      //      String description = getChildText( deserializeFrom, "description" );

      closeTag( deserializeFrom );

      return new Window( description, width, height );
    }
  }
}
