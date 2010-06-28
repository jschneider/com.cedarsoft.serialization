package com.cedarsoft.serialization.demo1;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.testng.Assert.*;

/**
 *
 */
public class MoneyTest {
  @NonNls
  public static final String EXPECTED_XML = "<money>\n" +
    "  <amount>7.01</amount>\n" +
    "</money>";

  private XStream xStream;

  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
  }

  @Test
  public void testXStream() {
    assertEquals( xStream.toXML( new Money( 7.01 ) ), EXPECTED_XML );
    assertEquals( ( ( Money ) xStream.fromXML( EXPECTED_XML ) ).getAmount(), 7.01 );
  }

  @Test
  public void testXStreamAttribute() {
    xStream.useAttributeFor( Money.class, "amount" );

    assertEquals( xStream.toXML( new Money( 7.01 ) ), "<money amount=\"7.01\"/>" );
    assertEquals( ( ( Money ) xStream.fromXML( "<money amount=\"7.01\"/>" ) ).getAmount(), 7.01 );
  }

  @Test
  public void testPrecision() {
    xStream.useAttributeFor( Money.class, "amount" );
    assertEquals( xStream.toXML( new Money( ( float ) 1.01 ) ), "<money amount=\"1.0099999904632568\"/>" );
  }

  @Test
  public void testSimple() throws XMLStreamException {
    assertEquals( serialize( new Money( 7.01 ) ), "<money><amount>7.01</amount></money>" );
    assertEquals( deserialize( new ByteArrayInputStream( "<money><amount>7.01</amount></money>".getBytes() ) ).getAmount(), 7.01 );
  }

  private static String serialize( @NotNull Money money ) throws XMLStreamException {
    //Boilerplate
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter( out );
    //    writer.writeStartDocument();

    //That is the actual code
    writer.writeStartElement( "money" );
    writer.writeStartElement( "amount" );
    writer.writeCharacters( String.valueOf( money.getAmount() ) );
    writer.writeEndElement();
    writer.writeEndElement();

    //Boiler plate
    writer.close();
    return out.toString();
  }

  private static Money deserialize( @NotNull InputStream serialized ) throws XMLStreamException {
    //Boilerplate
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader( serialized );
    //    writer.writeStartDocument();

    //That is the actual code
    reader.nextTag();//money
    reader.nextTag();//amount
    reader.next();//the content
    double amount = Double.parseDouble( reader.getText() );

    return new Money( amount );
  }
}
