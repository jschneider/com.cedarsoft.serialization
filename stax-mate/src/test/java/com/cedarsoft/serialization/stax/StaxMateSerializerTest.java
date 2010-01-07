package com.cedarsoft.serialization.stax;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.xml.XmlCommons;
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
public class StaxMateSerializerTest extends AbstractXmlSerializerTest<String> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<String> getSerializer() {
    return new AbstractStaxMateSerializer<String>( "aString", String.class, new VersionRange( new Version( 1, 5, 3 ), new Version( 1, 5, 3 ) ) ) {
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
  }

  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws Exception, IOException {
    super.verifySerialized( serialized );
    assertTrue( new String( serialized ).contains( "xmlns=\"http://www.lang.java/String/1.5.3\"" ), XmlCommons.format( new String( serialized ) ) );
  }

  @NotNull
  @Override
  protected String createObjectToSerialize() {
    return "asdf";
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<aString xmlns=\"http://www.lang.java/String/1.5.3\">asdf</aString>";
  }

  @Override
  protected void verifyDeserialized( @NotNull String deserialized ) {
    assertEquals( deserialized, "asdf" );
  }

  @Test
  public void testNoVersion() throws IOException {
    try {
      getSerializer().deserialize( new ByteArrayInputStream( "<aString>asdf</aString>".getBytes() ) );
      fail( "Where is the Exception" );
    } catch ( VersionException ignore ) {

    }
  }

  @Test
  public void testWrongVersion() throws IOException {
    try {
      getSerializer().deserialize( new ByteArrayInputStream( "<aString xmlns=\"http://www.lang.java/String/0.9.9\">asdf</aString>".getBytes() ) );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }
}
