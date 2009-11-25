package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 *
 */
public class StaxIntegerSerializer extends AbstractStaxSerializer<Integer> {
  public StaxIntegerSerializer() {
    super( "int", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @NotNull
  @Override
  public XMLStreamWriter serialize( @NotNull XMLStreamWriter serializeTo, @NotNull Integer object ) throws IOException, XMLStreamException {
    serializeTo.writeCharacters( object.toString() );
    return serializeTo;
  }

  @NotNull
  @Override
  public Integer deserialize( @NotNull XMLStreamReader deserializeFrom ) throws IOException, XMLStreamException {
    return Integer.parseInt( getText( deserializeFrom ) );
  }
}
