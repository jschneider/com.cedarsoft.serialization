package com.cedarsoft.serialization;

import com.cedarsoft.file.Extension;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class ExtensionSerializer2Test {
  @Test
  public void testDelimiter() throws IOException {
    Extension extension = deserialize(
      "<?format 1.0.0?>" +
        "  <extension delimiter=\".\">jpg</extension>\n" );

    assertEquals( extension.getDelimiter(), "." );
    assertEquals( extension.getExtension(), "jpg" );
  }

  @Test
  public void testMissingDelimiter() throws IOException {
    Extension extension = deserialize(
      "<?format 1.0.0?>" +
        "  <extension>jpg</extension>\n" );

    assertEquals( extension.getDelimiter(), "." );
    assertEquals( extension.getExtension(), "jpg" );
  }

  private Extension deserialize( String xml ) throws IOException {
    return new ExtensionSerializer().deserialize( new ByteArrayInputStream( xml.getBytes() ) );
  }
}