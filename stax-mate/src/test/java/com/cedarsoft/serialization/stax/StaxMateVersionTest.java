package com.cedarsoft.serialization.stax;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxMateVersionTest {
  @Test
  public void testOld() throws IOException, SAXException {
    OldIntegerSerializer serializer = new OldIntegerSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );
    AssertUtils.assertXMLEqual( out.toString(), "<integer xmlns=\"http://integer/1.0.0\" value=\"7\" />" );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), Integer.valueOf( 7 ) );
  }

  @Test
  public void testNew() throws IOException, SAXException {
    NewIntegerSerializer serializer = new NewIntegerSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );
    AssertUtils.assertXMLEqual( out.toString(), "<integer xmlns=\"http://integer/2.0.0\">7</integer>" );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), Integer.valueOf( 7 ) );
  }


  @Test
  public void testVersionsFail() throws IOException {
    OldIntegerSerializer serializer = new OldIntegerSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );

    try {
      new NewIntegerSerializer().deserialize( new ByteArrayInputStream( out.toByteArray() ) );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException e ) {
      assertEquals( e.getActual(), new Version( 1, 0, 0 ) );
      assertEquals( e.getExpected(), new Version( 2, 0, 0 ) );
    }

    assertEquals( new NewOldSerializer().deserialize( new ByteArrayInputStream( out.toByteArray() ) ), Integer.valueOf( 7 ) );
  }


  public static class NewOldSerializer extends AbstractStaxMateSerializer<Integer> {
    public NewOldSerializer() {
      super( "integer", "http://integer", new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object ) throws IOException, XMLStreamException {
      serializeTo.addCharacters( object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      if ( formatVersion.equals( new Version( 1, 0, 0 ) ) ) {
        int intValue = Integer.parseInt( deserializeFrom.getAttributeValue( null, "value" ) );
        closeTag( deserializeFrom );
        return intValue;
      } else if ( formatVersion.equals( new Version( 2, 0, 0 ) ) ) {
        return Integer.parseInt( getText( deserializeFrom ) );
      } else {
        throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
      }
    }
  }

  public static class NewIntegerSerializer extends AbstractStaxMateSerializer<Integer> {
    public NewIntegerSerializer() {
      super( "integer", "http://integer", new VersionRange( new Version( 2, 0, 0 ), new Version( 2, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object ) throws IOException, XMLStreamException {
      serializeTo.addCharacters( object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      return Integer.parseInt( getText( deserializeFrom ) );
    }
  }

  public static class OldIntegerSerializer extends AbstractStaxMateSerializer<Integer> {
    public OldIntegerSerializer() {
      super( "integer", "http://integer", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Integer object ) throws IOException, XMLStreamException {
      serializeTo.addAttribute( "value", object.toString() );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
      int intValue = Integer.parseInt( deserializeFrom.getAttributeValue( null, "value" ) );
      closeTag( deserializeFrom );
      return intValue;
    }
  }
}
