package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class ExtraSerializer extends AbstractStaxMateSerializer<Extra> {
  @NotNull
  private final MoneySerializer moneySerializer;

  // This serializier delegates the serialization of an object to another serializer

  public ExtraSerializer( MoneySerializer moneySerializer ) {
    super( "extra", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    this.moneySerializer = moneySerializer;

    //We verify the version here. This is necessary, to ensure that the file format for the
    //complete extra keeps constant.
    //If someone changes the MoneySerializer we have to take some manual steps and ensure
    //the changes are backwards compatible or handle the differences somehow.
    verifyDelegatingSerializerVersion( moneySerializer, new Version( 1, 0, 0 ) );
  }

  @NotNull
  @Override
  public SMOutputElement serialize( @NotNull SMOutputElement serializeTo, @NotNull Extra object ) throws IOException, XMLStreamException {
    serializeTo.addElement( "description" ).addCharacters( object.getDescription() );

    //We delegate the serialization of the price to the money serializer
    moneySerializer.serialize( serializeTo.addElement( "price" ), object.getPrice() );

    return serializeTo;
  }

  @NotNull
  @Override
  public Extra deserialize( @NotNull XMLStreamReader deserializeFrom ) throws IOException, XMLStreamException {
    String description = getChildText( deserializeFrom, "description" );

    nextTag( deserializeFrom, "price" );
    Money price = moneySerializer.deserialize( deserializeFrom );
    //closes the price tag automatically

    //we have to close our tag now
    closeTag( deserializeFrom );

    return new Extra( description, price );
  }
}
