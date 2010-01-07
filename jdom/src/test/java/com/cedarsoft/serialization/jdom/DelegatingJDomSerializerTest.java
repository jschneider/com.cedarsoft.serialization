package com.cedarsoft.serialization.jdom;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class DelegatingJDomSerializerTest extends AbstractXmlSerializerTest<Number> {
  private MySerializer serializer;

  @BeforeMethod
  protected void setUp() throws Exception {
    AbstractJDomSerializingStrategy<Integer> intSerializer = new AbstractJDomSerializingStrategy<Integer>( "int", Integer.class, new VersionRange( new Version( 1, 0, 1 ), new Version( 1, 0, 1 ) ) ) {
      @Override
      public void serialize( @NotNull Element serializeTo, @NotNull Integer object ) throws IOException {
        serializeTo.setText( object.toString() );
      }

      @Override
      @NotNull
      public Integer deserialize( @NotNull @NonNls Element deserializeFrom, @NotNull Version formatVersion ) throws IOException {
        return 1;
      }
    };
    AbstractJDomSerializingStrategy<Double> doubleSerializer = new AbstractJDomSerializingStrategy<Double>( "double", Double.class, new VersionRange( new Version( 1, 0, 2 ), new Version( 1, 0, 2 ) ) ) {
      @Override
      public void serialize( @NotNull Element serializeTo, @NotNull Double object ) throws IOException {
        serializeTo.setText( object.toString() );
      }

      @Override
      @NotNull
      public Double deserialize( @NotNull @NonNls Element deserializeFrom, @NotNull Version formatVersion ) throws IOException {
        return 2.0;
      }
    };
    serializer = new MySerializer( intSerializer, doubleSerializer );
  }

  @NotNull
  @Override
  protected AbstractJDomSerializer<Number> getSerializer() {
    return serializer;
  }

  @NotNull
  @Override
  protected Number createObjectToSerialize() {
    return 1;
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<number type=\"int\">1</number>";
  }

  @Override
  protected void verifyDeserialized( @NotNull Number deserialized ) {
    assertEquals( 1, deserialized );
  }

  @Test
  public void testIt() throws IOException, SAXException {
    assertEquals( serializer.getStrategies().size(), 2 );

    AssertUtils.assertXMLEqual( new String( serializer.serializeToByteArray( 1 ) ).trim(), "<number xmlns=\"http://number/1.2.3\" type=\"int\">1</number>" );
    AssertUtils.assertXMLEqual( new String( serializer.serializeToByteArray( 2.0 ) ).trim(), "<number xmlns=\"http://number/1.2.3\" type=\"double\">2.0</number>" );
  }

  public static class MySerializer extends AbstractDelegatingJDomSerializer<Number> {
    public MySerializer( @NotNull JDomSerializingStrategy<? extends Number>... serializingStrategies ) {
      super( "number", new VersionRange( new Version( 1, 2, 3 ), new Version( 1, 2, 3 ) ), serializingStrategies );
    }
  }
}
