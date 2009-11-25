package com.cedarsoft.serialization;

import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.xml.XmlCommons;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class FileNameSerializerTest extends AbstractXmlSerializerTest<FileName> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<FileName> getSerializer() {
    return new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
  }

  @NotNull
  @Override
  protected FileName createObjectToSerialize() {
    return new FileName( "a", ",", "pdf" );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<fileName>\n" +
      "  <baseName>a</baseName>\n" +
      "  <extension delimiter=\",\">pdf</extension>\n" +
      "</fileName>";
  }

  @Override
  protected void verifyDeserialized( @NotNull FileName fileName ) {
    assertEquals( fileName, createObjectToSerialize() );
  }

  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws SAXException, IOException {
    super.verifySerialized( serialized );
    assertTrue( new String( serialized ).contains( "<?format " + getSerializer().getFormatVersion() + "?>" ), XmlCommons.format( new String( serialized ) ) );
  }
}
