package com.cedarsoft.serialization.stax;

import org.testng.annotations.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxMateMultiNamespaceTest {
  @Test
  public void testNs() throws Exception {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader(
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<fileType xmlns=\"dans1\" dependent=\"false\">\n" +
        "  <id>Canon Raw</id>\n" +
        "  <extension xmlns=\"dans2\" default=\"true\" delimiter=\".\">cr2</extension>\n" +
        "</fileType>" ) );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getNamespaceURI(), "dans1" );
    assertEquals( parser.getLocalName(), "fileType" );
    assertEquals( parser.getName().getLocalPart(), "fileType" );
    assertEquals( parser.getAttributeValue( null, "dependent" ), "false" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getLocalName(), "id" );
    assertEquals( parser.getElementText(), "Canon Raw" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getNamespaceURI(), "dans2" );
    assertEquals( parser.getLocalName(), "extension" );
    assertEquals( parser.getName().getLocalPart(), "extension" );
    assertEquals( parser.getAttributeValue( null, "default" ), "true" );
    assertEquals( parser.getAttributeValue( null, "delimiter" ), "." );
    assertEquals( parser.getElementText(), "cr2" );
  }

  @Test
  public void testNs2() throws Exception {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader(
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<fileType xmlns=\"dans1\" dependent=\"false\">\n" +
        "  <id>Canon Raw</id>\n" +
        "  <extension xmlns=\"dans2\" default=\"true\" delimiter=\".\">" +
        "   <asdf/>" +
        " </extension>\n" +
        "</fileType>" ) );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getNamespaceURI(), "dans1" );
    assertEquals( parser.getLocalName(), "fileType" );
    assertEquals( parser.getName().getLocalPart(), "fileType" );
    assertEquals( parser.getAttributeValue( null, "dependent" ), "false" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getLocalName(), "id" );
    assertEquals( parser.getElementText(), "Canon Raw" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getNamespaceURI(), "dans2" );
    assertEquals( parser.getLocalName(), "extension" );
    assertEquals( parser.getName().getLocalPart(), "extension" );
    assertEquals( parser.getAttributeValue( null, "default" ), "true" );
    assertEquals( parser.getAttributeValue( null, "delimiter" ), "." );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getNamespaceURI(), "dans2" );
    assertEquals( parser.getLocalName(), "asdf" );
  }
}
