package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * This serializer delegates the serialization of an object to another serializer
 */
public class ExtraSerializer extends AbstractStaxMateSerializer<Extra> {
  //START SNIPPET: fieldsAndConstructors

  public ExtraSerializer( MoneySerializer moneySerializer ) {
    super( "extra", "http://www.cedarsoft.com/test/extra", new VersionRange( new Version( 1, 5, 0 ), new Version( 1, 5, 0 ) ) );
    //We choose another version number. Maybe this is an old serializer that has been created within another project.

    add( moneySerializer ).responsibleFor( Money.class )
      .map( 1, 5, 0 ).toDelegateVersion( 1, 0, 0 )
      ;

    //We verify the version here. This is necessary, to ensure that the file format for the
    //complete extra keeps constant.
    //If someone changes the MoneySerializer we have to take some manual steps and ensure
    //the changes are backwards compatible or handle the differences somehow.
    getDelegatesMappings().verify();
  }
  //END SNIPPET: fieldsAndConstructors

  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Extra object ) throws IOException, XMLStreamException {
    serializeTo.addElement( serializeTo.getNamespace(), "description" ).addCharacters( object.getDescription() );

    //We delegate the serialization of the price to the money serializer
    serialize( object.getPrice(), Money.class, serializeTo.addElement( serializeTo.getNamespace(), "price" ) );
  }

  @Override
  public Extra deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    String description = getChildText( deserializeFrom, "description" );

    nextTag( deserializeFrom, "price" );
    Money price = deserialize( Money.class, formatVersion, deserializeFrom );
    //closes the price tag automatically

    //we have to close our tag now
    closeTag( deserializeFrom );

    return new Extra( description, price );
  }
  //END SNIPPET: serialize
}
