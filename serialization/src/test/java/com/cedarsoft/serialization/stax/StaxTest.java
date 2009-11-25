package com.cedarsoft.serialization.stax;

import com.cedarsoft.AssertUtils;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxTest {
  @NotNull
  @NonNls
  public static final String CONTENT_SAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<fileType dependent=\"false\">\n" +
    "  <id>Canon Raw</id>\n" +
    "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
    "</fileType>";

  @Test
  public void testProcssingInstructions() throws Exception {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    doc.setIndentation( "\n  ", 1, 2 );
    doc.addProcessingInstruction( "format", "version=\"1.0\"" );

    SMOutputElement fileTypeElement = doc.addElement( "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    AssertUtils.assertXMLEqual( out.toString(), CONTENT_SAMPLE, false );
    assertTrue( out.toString().contains( "<?format version=\"1.0\"?>" ), out.toString() );


    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader( out.toString() ) );

    assertEquals( parser.next(), XMLStreamReader.PROCESSING_INSTRUCTION );
    assertEquals( parser.getPITarget(), "format" );
    assertEquals( parser.getPIData(), "version=\"1.0\"" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getLocalName(), "fileType" );
    assertEquals( parser.getName().getLocalPart(), "fileType" );
    assertEquals( parser.getAttributeValue( null, "dependent" ), "false" );
  }

  @Test
  public void testStaxMate() throws XMLStreamException, IOException, SAXException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    doc.setIndentation( "\n  ", 1, 2 );

    SMOutputElement fileTypeElement = doc.addElement( "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    AssertUtils.assertXMLEqual( out.toString(), CONTENT_SAMPLE, false );
  }

  @Test
  public void testStaxMateRead() throws XMLStreamException {
    SMInputFactory smInputFactory = new SMInputFactory( XMLInputFactory.newInstance() );
//    XMLStreamReader reader = smInputFactory.createStax2Reader( new StringReader( CONTENT_SAMPLE ) );
    XMLStreamReader reader = smInputFactory.getStaxFactory().createXMLStreamReader( new StringReader( CONTENT_SAMPLE ) );

    assertEquals( reader.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( reader.getLocalName(), "fileType" );
    assertEquals( reader.getName().getLocalPart(), "fileType" );
    assertEquals( reader.getAttributeValue( null, "dependent" ), "false" );

    assertEquals( reader.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( reader.getName().getLocalPart(), "id" );
    assertEquals( reader.next(), XMLStreamReader.CHARACTERS );
    assertEquals( reader.getText(), "Canon Raw" );
    assertEquals( reader.nextTag(), XMLStreamReader.END_ELEMENT );
    assertEquals( reader.getName().getLocalPart(), "id" );

    assertEquals( reader.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( reader.getName().getLocalPart(), "extension" );
    assertEquals( reader.getAttributeValue( null, "default" ), "true" );
    assertEquals( reader.getAttributeValue( null, "delimiter" ), "." );
    assertEquals( reader.next(), XMLStreamReader.CHARACTERS );
    assertEquals( reader.getText(), "cr2" );
    assertEquals( reader.nextTag(), XMLStreamReader.END_ELEMENT );
    assertEquals( reader.getName().getLocalPart(), "extension" );

    assertEquals( reader.nextTag(), XMLStreamReader.END_ELEMENT );
    assertEquals( reader.getName().getLocalPart(), "fileType" );
    assertEquals( reader.next(), XMLStreamReader.END_DOCUMENT );

    try {
      reader.next();
      fail( "Where is the Exception" );
    } catch ( NoSuchElementException ignore ) {
    }
  }

  @Test
  public void testBug() throws XMLStreamException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    assertEquals( factory.getProperty( XMLOutputFactory.IS_REPAIRING_NAMESPACES ), false );
    factory.setProperty( XMLOutputFactory.IS_REPAIRING_NAMESPACES, false );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = factory.createXMLStreamWriter( out );

    assertEquals( writer.getProperty( XMLOutputFactory.IS_REPAIRING_NAMESPACES ), false );
  }

  @Test
  public void testWrite() throws XMLStreamException, IOException, SAXException {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = factory.createXMLStreamWriter( out );

    writer.writeStartDocument();
    writer.writeStartElement( "fileType" );
    writer.writeAttribute( "dependent", "false" );

    writer.writeStartElement( "id" );
    writer.writeCharacters( "Canon Raw" );
    writer.writeEndElement();

    writer.writeStartElement( "extension" );
    writer.writeAttribute( "default", "true" );
    writer.writeAttribute( "delimiter", "." );
    writer.writeCharacters( "cr2" );
    writer.writeEndElement();

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.close();

    AssertUtils.assertXMLEqual( out.toString(), CONTENT_SAMPLE, true );
  }

  @Test
  public void testStax() throws XMLStreamException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader( CONTENT_SAMPLE ) );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getLocalName(), "fileType" );
    assertEquals( parser.getName().getLocalPart(), "fileType" );
    assertEquals( parser.getAttributeValue( null, "dependent" ), "false" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getName().getLocalPart(), "id" );
    assertEquals( parser.next(), XMLStreamReader.CHARACTERS );
    assertEquals( parser.getText(), "Canon Raw" );
    assertEquals( parser.nextTag(), XMLStreamReader.END_ELEMENT );
    assertEquals( parser.getName().getLocalPart(), "id" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getName().getLocalPart(), "extension" );
    assertEquals( parser.getAttributeValue( null, "default" ), "true" );
    assertEquals( parser.getAttributeValue( null, "delimiter" ), "." );
    assertEquals( parser.next(), XMLStreamReader.CHARACTERS );
    assertEquals( parser.getText(), "cr2" );
    assertEquals( parser.nextTag(), XMLStreamReader.END_ELEMENT );
    assertEquals( parser.getName().getLocalPart(), "extension" );

    assertEquals( parser.nextTag(), XMLStreamReader.END_ELEMENT );
    assertEquals( parser.getName().getLocalPart(), "fileType" );
    assertEquals( parser.next(), XMLStreamReader.END_DOCUMENT );
  }

  @Test
  public void testIterator() throws XMLStreamException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    assertEquals( inputFactory.getProperty( XMLInputFactory.IS_COALESCING ), false );
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    assertEquals( inputFactory.getProperty( XMLInputFactory.IS_COALESCING ), true );
    //    inputFactory.setProperty(  );

    XMLEventReader parser = inputFactory.createXMLEventReader( new StringReader( CONTENT_SAMPLE ) );

    {
      XMLEvent event = parser.nextEvent();
      assertEquals( event.getEventType(), XMLEvent.START_DOCUMENT );
    }

    {
      XMLEvent event = parser.nextEvent();
      assertEquals( event.getEventType(), XMLEvent.START_ELEMENT );
      assertEquals( event.asStartElement().getName().getLocalPart(), "fileType" );
      assertEquals( event.asStartElement().getAttributeByName( new QName( null, "dependent" ) ).getValue(), "false" );
    }
  }
}
