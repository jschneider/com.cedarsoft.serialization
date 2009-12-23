package com.cedarsoft.serialization.bench;

import com.cedarsoft.AssertUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.testng.Assert.*;

/**
 *
 */
public class SimpleXmlTest {
  @Test
  public void testIt() throws Exception {
    Serializer serializer = new Persister();

    XmlParserPerformance.FileType fileType = new XmlParserPerformance.FileType( "Canon Raw", new XmlParserPerformance.Extension( ".", "cr2", true ), false );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write( fileType, out );

    AssertUtils.assertXMLEqual( out.toString(), XmlParserPerformance.CONTENT_SAMPLE_XSTREAM );
  }

  @Test
  public void testDeserialize() throws Exception {
    Serializer serializer = new Persister();

    XmlParserPerformance.FileType read = serializer.read( XmlParserPerformance.FileType.class, new ByteArrayInputStream( XmlParserPerformance.CONTENT_SAMPLE_XSTREAM.getBytes() ) );
    assertNotNull( read.getExtension() );
  }
}

