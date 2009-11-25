package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class ExtensionSerializer extends AbstractStaxMateSerializer<Extension> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_DELIMITER = "delimiter";

  public ExtensionSerializer() {
    super( "extension", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @NotNull
  @Override
  public SMOutputElement serialize( @NotNull SMOutputElement serializeTo, @NotNull Extension object ) throws IOException, XMLStreamException {
    serializeTo.addAttribute( ATTRIBUTE_DELIMITER, object.getDelimiter() );
    serializeTo.addCharacters( object.getExtension() );
    return serializeTo;
  }

  @NotNull
  @Override
  public Extension deserialize( @NotNull XMLStreamReader deserializeFrom ) throws IOException, XMLStreamException {
    String delimiter = deserializeFrom.getAttributeValue( null, ATTRIBUTE_DELIMITER );

    deserializeFrom.next();
    String extension = deserializeFrom.getText();

    closeTag( deserializeFrom );

    return new Extension( delimiter, extension );
  }
}
