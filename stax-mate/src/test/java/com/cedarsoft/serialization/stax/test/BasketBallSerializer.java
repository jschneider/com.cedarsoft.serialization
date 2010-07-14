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
public class BasketBallSerializer extends AbstractStaxMateSerializingStrategy<BasketBall> {
  public BasketBallSerializer() {
    super( "basketBall", "http://test/basketball", BasketBall.class, VersionRange.from( 2, 0, 0 ).to( 2, 0, 1 ) );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull BasketBall object, @NotNull Version formatVersion, @NotNull SerializationContext context ) throws IOException, XMLStreamException {
    verifyVersionReadable( formatVersion );
    serializeTo.addAttribute( "theId", String.valueOf( object.getTheId() ) );
  }

  @NotNull
  @Override
  public BasketBall deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException, VersionException, XMLStreamException {
    verifyVersionReadable( formatVersion );

    String theId;
    if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
      theId = getText( deserializeFrom );
    } else {
      theId = deserializeFrom.getAttributeValue( null, "theId" );
      closeTag( deserializeFrom );
    }

    return new BasketBall( theId );
  }
}
