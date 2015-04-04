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
public class CharSerializer extends AbstractStaxMateSerializer<Character> {
  @Inject
  public CharSerializer() {
    super( "char", "http://cedarsoft.com/primitives", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Character object, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.addCharacters( String.valueOf( object ) );
  }

  @Nonnull
  @Override
  public Character deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    String text = getText( deserializeFrom ).trim();
    if ( text.length() != 1 ) {
      throw new IllegalStateException( "Cannot convert <" + text + "> to char" );
    }
    return text.charAt( 0 );
  }
}
