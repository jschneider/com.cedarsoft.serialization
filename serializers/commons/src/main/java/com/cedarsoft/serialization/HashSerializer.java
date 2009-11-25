package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class HashSerializer extends AbstractStaxMateSerializer<Hash> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_ALGORITHM = "algorithm";

  public HashSerializer() {
    super( "hash", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @NotNull
  @Override
  public SMOutputElement serialize( @NotNull SMOutputElement serializeTo, @NotNull Hash object ) throws IOException, XMLStreamException {
    serializeTo.addAttribute( ATTRIBUTE_ALGORITHM, object.getAlgorithm().name() );
    serializeTo.addCharacters( object.getValueAsHex() );
    return serializeTo;
  }

  @NotNull
  @Override
  public Hash deserialize( @NotNull XMLStreamReader deserializeFrom ) throws IOException, XMLStreamException {
    String algorithm = deserializeFrom.getAttributeValue( null, ATTRIBUTE_ALGORITHM );
    String valueAsHex = getText( deserializeFrom );

    return Hash.fromHex( Algorithm.valueOf( algorithm ), valueAsHex );
  }
}
