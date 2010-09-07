package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class EmailSerializer extends AbstractStaxSerializer<Email> {
  @NonNls
  public static final String DEFAULT_ELEMENT_NAME = "email";

  public EmailSerializer() {
    super( DEFAULT_ELEMENT_NAME, "http://test/email", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull XMLStreamWriter serializeTo, @NotNull Email object, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.writeCharacters( object.getMail() );
  }

  @NotNull
  @Override
  public Email deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    return new Email( getText( deserializeFrom ) );
  }
}
