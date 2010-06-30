package com.cedarsoft.serialization.demo2;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

import static com.cedarsoft.AssertUtils.assertXMLEqual;
import static org.testng.Assert.*;

/**
 *
 */
public class MoneyXstreamLegacyTest {
  private XStream xStream;

  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
  }

  @Test
  public void testFromXml() throws IOException {
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream() ) ).getCents(), 701 );
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream() ) ).getAmount(), 7.01 );
  }

  @Test
  public void testFromXmlLegacyFormat() throws IOException {
    try {
      xStream.fromXML( com.cedarsoft.serialization.demo1.MoneyTest.EXPECTED.openStream() );
      fail( "Where is the Exception" );
    } catch ( ConversionException e ) {
      assertEquals( e.getMessage(), "amount : amount : amount : amount\n" +
        "---- Debugging information ----\n" +
        "message             : amount : amount\n" +
        "cause-exception     : com.thoughtworks.xstream.mapper.CannotResolveClassException\n" +
        "cause-message       : amount : amount\n" +
        "class               : com.cedarsoft.serialization.demo2.Money\n" +
        "required-type       : com.cedarsoft.serialization.demo2.Money\n" +
        "path                : /money/amount\n" +
        "line number         : 3\n" +
        "-------------------------------" );
    }
  }

  @Test
  public void testCustomConverter() throws IOException, SAXException {
    xStream.registerConverter( new MoneyConverter() );
    //writing
    assertXMLEqual( xStream.toXML( new Money( 701 ) ), com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED );
    //current format
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream() ) ).getCents(), 701 );
    //old format
    assertEquals( ( ( Money ) xStream.fromXML( com.cedarsoft.serialization.demo1.MoneyTest.EXPECTED.openStream() ) ).getCents(), 701 );
  }

  public static class MoneyConverter implements Converter {
    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
      writer.startNode( "cents" );
      writer.setValue( String.valueOf( ( ( Money ) source ).getCents() ) );
      writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
      reader.moveDown();
      long cents;
      if ( reader.getNodeName().equals( "amount" ) ) {
        //Legacy!
        cents = Money.convertValueToCents( Double.parseDouble( reader.getValue() ) );
      } else {
        cents = Long.parseLong( reader.getValue() );
      }
      reader.getValue();
      reader.moveUp();

      return new Money( cents );
    }

    @Override
    public boolean canConvert( Class type ) {
      return type.equals( Money.class );
    }
  }

  @Test
  public void testManual() throws XMLStreamException, IOException, SAXException {
    assertEquals( deserialize( com.cedarsoft.serialization.demo2.MoneyTest.EXPECTED.openStream(), Version.CURRENT ).getCents(), 701 );
    assertEquals( deserialize( com.cedarsoft.serialization.demo1.MoneyTest.EXPECTED.openStream(), Version.LEGACY ).getCents(), 701 );
  }

  private static Money deserialize( @NotNull InputStream serialized, Version version ) throws XMLStreamException {
    //Boilerplate
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader( serialized );
    //    writer.writeStartDocument();

    //That is the actual code
    reader.nextTag();//money
    reader.nextTag();//cents or amount
    reader.next();//the content

    long cents;
    if ( version == Version.LEGACY ) {
      cents = Money.convertValueToCents( Double.parseDouble( reader.getText() ) );
    } else {
      cents = Long.parseLong( reader.getText() );
    }

    return new Money( cents );
  }

  public enum Version {
    LEGACY,
    CURRENT
  }
}
