package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.BaseName;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class BaseNameSerializer extends AbstractStaxMateSerializer<BaseName> {
  public BaseNameSerializer() {
    super( "baseName", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull BaseName object ) throws IOException, XMLStreamException {
    serializeTo.addCharacters( object.getName() );

  }

  @NotNull
  @Override
  public BaseName deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    return new BaseName( getText( deserializeFrom ) );
  }
}
