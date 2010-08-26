package com.cedarsoft.serialization.bench;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.junit.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JsonTest {
  @Test
  public void testIt() throws Exception {
    StringWriter strWriter = new StringWriter();

    // Mapped convention
    MappedNamespaceConvention con = new MappedNamespaceConvention();
    XMLStreamWriter w = new MappedXMLStreamWriter( con, strWriter );
    // XMLStreamWriter w = new BadgerFishXMLStreamWriter(strWriter);

    w.writeStartDocument();

    w.writeStartElement( "fileType" );
    w.writeAttribute( "dependent", "false" );

    w.writeStartElement( "id" );
    w.writeCharacters( "Canon Raw" );
    w.writeEndElement();

    w.writeStartElement( "extension" );
    w.writeAttribute( "default", "true" );
    w.writeAttribute( "delimiter", "." );
    w.writeCharacters( "cr2" );
    w.writeEndElement();

    w.writeEndElement();
    w.writeEndDocument();

    w.close();
    strWriter.close();

    assertEquals( "{\"fileType\":{\"@dependent\":\"false\",\"id\":\"Canon Raw\",\"extension\":{\"@default\":\"true\",\"@delimiter\":\".\",\"$\":\"cr2\"}}}", strWriter.toString() );
  }

//
//  "<fileType dependent=\"false\">\n" +
//  "  <id>Canon Raw</id>\n" +
//  "  <extension default=\"true\" delimiter=\".\">cr2</extension>\n" +
//  "</fileType>";

}
