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
public class DoubleSerializer extends AbstractStaxMateSerializer<Double> {
  @Inject
  public DoubleSerializer() {
    super( "double", "http://cedarsoft.com/primitives", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Double object, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.addCharacters( String.valueOf( object ) );
  }

  @Nonnull
  @Override
  public Double deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    return Double.parseDouble( getText( deserializeFrom ) );
  }
}
