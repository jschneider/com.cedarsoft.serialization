package com.cedarsoft.serialization.stax.mate.primitives;

import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ShortSerializer extends AbstractStaxMateSerializer<Short> {
  @Inject
  public ShortSerializer() {
    super( "short", "http://cedarsoft.com/primitives", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Short object, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.addCharacters( String.valueOf( object ) );
  }

  @Nonnull
  @Override
  public Short deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    return Short.parseShort( getText( deserializeFrom ) );
  }
}
