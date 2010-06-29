package com.cedarsoft.serialization.demo1;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.cedarsoft.AssertUtils.assertXMLEqual;
import static org.testng.Assert.*;

/**
 *
 */
public class MoneyTest {
  @NotNull
  public static final URL EXPECTED = MoneyTest.class.getResource( "money.xml" );
  @NotNull
  public static final URL EXPECTED_ATTRIBUTE = MoneyTest.class.getResource( "money_attribute.xml" );

  private XStream xStream;

  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
  }

  @Test
  public void testXStream() throws IOException, SAXException {
    assertXMLEqual( xStream.toXML( new Money( 7.01 ) ), EXPECTED );
    assertEquals( ( ( Money ) xStream.fromXML( EXPECTED.openStream() ) ).getAmount(), 7.01 );
  }

  @Test
  public void testXStreamAttribute() throws IOException, SAXException {
    xStream.useAttributeFor( Money.class, "amount" );

    assertXMLEqual( xStream.toXML( new Money( 7.01 ) ), EXPECTED_ATTRIBUTE );
    assertEquals( ( ( Money ) xStream.fromXML( EXPECTED_ATTRIBUTE.openStream() ) ).getAmount(), 7.01 );
  }

  @Test
  public void testPrecision() {
    xStream.useAttributeFor( Money.class, "amount" );
    assertEquals( xStream.toXML( new Money( ( float ) 1.01 ) ), "<money amount=\"1.0099999904632568\"/>" );
  }

  @Test
  public void testManual() throws IOException, SAXException {
    assertXMLEqual( serializeSimple( new Money( 7.01 ) ), EXPECTED );
  }

  public static String serializeSimple( Money money ) {
    return "<money><amount>" + money.getAmount() + "</amount></money>";
  }

  @Test
  public void testSimple() throws XMLStreamException, IOException, SAXException {
    assertXMLEqual( serialize( new Money( 7.01 ) ), EXPECTED );
    assertEquals( deserialize( EXPECTED.openStream() ).getAmount(), 7.01 );
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
