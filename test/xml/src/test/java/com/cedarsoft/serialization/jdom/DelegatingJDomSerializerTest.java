package com.cedarsoft.serialization.jdom;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
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
public class DelegatingJDomSerializerTest extends AbstractJDomSerializerTest<Number, Object> {
  private MySerializer serializer;

  @BeforeMethod
  protected void setUp() throws Exception {
    AbstractJDomSerializingStrategy<Integer> intSerializer = new AbstractJDomSerializingStrategy<Integer>( "int", Integer.class, new VersionRange( new Version( 1, 0, 1 ), new Version( 1, 0, 1 ) ) ) {
      @Override
      @NotNull
      public Element serialize( @NotNull Element element, @NotNull Integer object ) throws IOException {
        element.setText( object.toString() );
        return element;
      }

      @Override
      @NotNull
      public Integer deserialize( @NotNull @NonNls Element element ) throws IOException {
        return 1;
      }
    };
    AbstractJDomSerializingStrategy<Double> doubleSerializer = new AbstractJDomSerializingStrategy<Double>( "double", Double.class, new VersionRange( new Version( 1, 0, 2 ), new Version( 1, 0, 2 ) ) ) {
      @Override
      @NotNull
      public Element serialize( @NotNull Element element, @NotNull Double object ) throws IOException {
        element.setText( object.toString() );
        return element;
      }

      @Override
      @NotNull
      public Double deserialize( @NotNull @NonNls Element element ) throws IOException {
        return 2.0;
      }
    };
    serializer = new MySerializer( intSerializer, doubleSerializer );
  }

  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws SAXException, IOException {
    super.verifySerialized( serialized );
    assertEquals( new String( serialized ).trim(), getExpectedSerialized().trim() );
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
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?format 1.2.3?>\n" +
      "<number type=\"int\">1</number>";
  }

  @Override
  protected void verifyDeserialized( @NotNull Number deserialized ) {
    assertEquals( 1, deserialized );
  }

  @Test
  public void tetIt() throws IOException, SAXException {
    assertEquals( serializer.getStrategies().size(), 2 );

    AssertUtils.assertXMLEqual( new String( serializer.serializeToByteArray( 1 ) ).trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<number type=\"int\">1</number>" );
    AssertUtils.assertXMLEqual( new String( serializer.serializeToByteArray( 2.0 ) ).trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<number type=\"double\">2.0</number>" );
  }

  public static class MySerializer extends AbstractDelegatingJDomSerializer<Number> {
    public MySerializer( @NotNull JDomSerializingStrategy<? extends Number>... serializingStrategies ) {
      super( "number", new VersionRange( new Version( 1, 2, 3 ), new Version( 1, 2, 3 ) ), serializingStrategies );
    }
  }
}
