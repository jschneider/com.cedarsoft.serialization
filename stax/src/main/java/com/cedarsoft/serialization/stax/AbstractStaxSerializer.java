package com.cedarsoft.serialization.stax;

import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for serializer using stax.
 *
 * @param <T> the type
 */
public abstract class AbstractStaxSerializer<T> extends AbstractStaxBasedSerializer<T, XMLStreamWriter> {
  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param formatVersionRange the format version range
   */
  protected AbstractStaxSerializer( @NotNull @NonNls String defaultElementName, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, formatVersionRange );
  }

  protected AbstractStaxSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull Class<? super T> typeForNameSpaceUri, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, typeForNameSpaceUri, formatVersionRange );
  }

  protected AbstractStaxSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    try {
      XMLStreamWriter writer = StaxSupport.getXmlOutputFactory().createXMLStreamWriter( out );

      //Sets the name space
      String nameSpace = createNameSpaceUri( getFormatVersion() );
      writer.setDefaultNamespace( nameSpace );

      writer.writeStartElement( getDefaultElementName() );
      writer.writeDefaultNamespace( nameSpace );

      serialize( writer, object );
      writer.writeEndElement();

      writer.close();
    } catch ( XMLStreamException e ) {
      throw new IOException( e );
    }
  }
}
