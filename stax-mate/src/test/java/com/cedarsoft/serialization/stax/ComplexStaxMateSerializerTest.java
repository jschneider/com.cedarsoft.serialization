package com.cedarsoft.serialization.stax;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;
import org.testng.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class ComplexStaxMateSerializerTest extends AbstractXmlSerializerTest<String> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<String> getSerializer() {
    final AbstractStaxMateSerializer<String> stringSerializer = new AbstractStaxMateSerializer<String>( "asdf", "asdf",new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @NotNull SMOutputElement serializeTo, @NotNull String object ) throws XMLStreamException {
        serializeTo.addCharacters( object );
      }

      @Override
      @NotNull
      public String deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws XMLStreamException {
        deserializeFrom.next();
        String text = deserializeFrom.getText();
        closeTag( deserializeFrom );
        return text;
      }
    };

    return new AbstractStaxMateSerializer<String>( "aString","asdf", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @NotNull SMOutputElement serializeTo, @NotNull String object ) throws IOException, XMLStreamException {
        stringSerializer.serialize( serializeTo.addElement( serializeTo.getNamespace(), "sub" ), object );
        serializeTo.addElement( serializeTo.getNamespace(), "emptyChild" ).addCharacters( "" );
      }

      @Override
      @NotNull
      public String deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
        nextTag( deserializeFrom, "sub" );
        String string = stringSerializer.deserialize( deserializeFrom, formatVersion );

        Assert.assertEquals( getChildText( deserializeFrom, "emptyChild" ), "" );
        closeTag( deserializeFrom );

        return string;
      }
    };
  }

  @NotNull
  @Override
  protected String createObjectToSerialize() {
    return "asdf";
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<aString><sub>asdf</sub><emptyChild/></aString>";
  }
}