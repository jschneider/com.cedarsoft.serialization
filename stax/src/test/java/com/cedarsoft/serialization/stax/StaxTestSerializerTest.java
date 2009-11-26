package com.cedarsoft.serialization.stax;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxTestSerializerTest extends AbstractXmlSerializerTest<Integer> {
  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<int>7</int>";
  }

  @NotNull
  @Override
  protected Serializer<Integer> getSerializer() {
    return new StaxIntegerSerializer();
  }

  @NotNull
  @Override
  protected Integer createObjectToSerialize() {
    return 7;
  }

  @Test
  public void testIt() {
    assertEquals( XMLInputFactory.newInstance( "com.sun.xml.internal.stream.XMLInputFactoryImpl", getClass().getClassLoader() ).getClass().getName(), "com.sun.xml.internal.stream.XMLInputFactoryImpl" );
    assertEquals( XMLInputFactory.newInstance( "com.sun.xml.internal.stream.XMLInputFactoryImpl", getClass().getClassLoader() ).getClass().getName(), "com.sun.xml.internal.stream.XMLInputFactoryImpl" );
  }


  public static class StaxIntegerSerializer extends AbstractStaxSerializer<Integer> {
    public StaxIntegerSerializer() {
      super( "int", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @NotNull
    @Override
    public XMLStreamWriter serialize( @NotNull XMLStreamWriter serializeTo, @NotNull Integer object ) throws IOException, XMLStreamException {
      serializeTo.writeCharacters( object.toString() );
      return serializeTo;
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull XMLStreamReader deserializeFrom ) throws IOException, XMLStreamException {
      return Integer.parseInt( getText( deserializeFrom ) );
    }
  }

}
