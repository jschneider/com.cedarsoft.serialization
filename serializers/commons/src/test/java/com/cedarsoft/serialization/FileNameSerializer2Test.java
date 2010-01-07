package com.cedarsoft.serialization;

import com.cedarsoft.file.FileName;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class FileNameSerializer2Test {
  @Test
  public void testDelimiter() throws IOException {
    FileName fileName = deserialize(
      "<fileName xmlns=\"http://www.cedarsoft.com/file/fileName/1.0.0\">\n" +
        "  <baseName>baseName</baseName>\n" +
        "  <extension delimiter=\".\">jpg</extension>\n" +
        "</fileName>" );

    assertEquals( fileName.getBaseName().getName(), "baseName" );
    assertEquals( fileName.getExtension().getDelimiter(), "." );
    assertEquals( fileName.getExtension().getExtension(), "jpg" );
  }

  @Test
  public void testMissingDelimiter() throws IOException {
    FileName fileName = deserialize(
      "<fileName xmlns=\"http://www.cedarsoft.com/file/fileName/1.0.0\">\n" +
        "  <baseName>baseName</baseName>\n" +
        "  <extension>jpg</extension>\n" +
        "</fileName>" );

    assertEquals( fileName.getBaseName().getName(), "baseName" );
    assertEquals( fileName.getExtension().getDelimiter(), "." );
    assertEquals( fileName.getExtension().getExtension(), "jpg" );
  }

  private FileName deserialize( String xml ) throws IOException {
    FileNameSerializer serializer = new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
    return serializer.deserialize( new ByteArrayInputStream( xml.getBytes() ) );
  }
}