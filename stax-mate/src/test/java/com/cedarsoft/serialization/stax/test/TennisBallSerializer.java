package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.SerializationContext;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializingStrategy;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class TennisBallSerializer extends AbstractStaxMateSerializingStrategy<TennisBall> {
  public TennisBallSerializer() {
    super( "tennisBall", "http://test/tennisball", TennisBall.class, VersionRange.from( 1, 5, 0 ).to( 1, 5, 1 ) );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull TennisBall object, @NotNull Version formatVersion, @NotNull SerializationContext context ) throws IOException, XMLStreamException {
    verifyVersionReadable( formatVersion );
    serializeTo.addAttribute( "id", String.valueOf( object.getId() ) );
  }

  @NotNull
  @Override
  public TennisBall deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException, VersionException, XMLStreamException {
    verifyVersionReadable( formatVersion );

    int id;
    if ( formatVersion.equals( Version.valueOf( 1, 5, 0 ) ) ) {
      id = Integer.parseInt( getText( deserializeFrom ) );
    } else {
      id = Integer.parseInt( deserializeFrom.getAttributeValue( null, "id" ) );
      closeTag( deserializeFrom );
    }

    return new TennisBall( id );
  }
}
