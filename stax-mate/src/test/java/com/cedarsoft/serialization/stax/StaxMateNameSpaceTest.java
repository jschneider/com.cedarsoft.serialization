package com.cedarsoft.serialization.stax;

import com.cedarsoft.AssertUtils;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.testng.annotations.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxMateNameSpaceTest {
  @Test
  public void testBasic() throws Exception {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    SMOutputFactory smOutputFactory = new SMOutputFactory( factory );

    SMOutputDocument doc = smOutputFactory.createOutputDocument( out );
    doc.setIndentation( "\n  ", 1, 2 );

    SMNamespace namespace = doc.getNamespace( "http://www.cedarsoft.com/serialization/filetype/1.0.1" );
    SMOutputElement fileTypeElement = doc.addElement( namespace, "fileType" );
    fileTypeElement.addAttribute( "dependent", "false" );

    SMOutputElement idElement = fileTypeElement.addElement( namespace, "id" );
    idElement.addCharacters( "Canon Raw" );

    SMOutputElement extensionElement = fileTypeElement.addElement( namespace, "extension" );
    extensionElement.addAttribute( "default", "true" );
    extensionElement.addAttribute( "delimiter", "." );
    extensionElement.addCharacters( "cr2" );

    doc.closeRoot();

    AssertUtils.assertXMLEqual( out.toString(),
                                "<fileType xmlns=\"http://www.cedarsoft.com/serialization/filetype/1.0.1\" dependent=\"false\">\n" +
                                  "  <id>Canon Raw</id>\n" +
                                  "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
                                  "</fileType>", false );


    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty( XMLInputFactory.IS_COALESCING, true );
    XMLStreamReader parser = inputFactory.createXMLStreamReader( new StringReader( out.toString() ) );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getLocalName(), "fileType" );
    assertEquals( parser.getNamespaceURI(), "http://www.cedarsoft.com/serialization/filetype/1.0.1" );
    assertEquals( parser.getName().getLocalPart(), "fileType" );
    assertEquals( parser.getAttributeValue( null, "dependent" ), "false" );

    assertEquals( parser.nextTag(), XMLStreamReader.START_ELEMENT );
    assertEquals( parser.getLocalName(), "id" );
    assertEquals( parser.getNamespaceURI(), "http://www.cedarsoft.com/serialization/filetype/1.0.1" );
    assertEquals( parser.getName().getLocalPart(), "id" );
  }
}
