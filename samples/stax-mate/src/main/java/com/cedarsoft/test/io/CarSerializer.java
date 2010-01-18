package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Model;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class CarSerializer extends AbstractStaxMateSerializer<Car> {
  //START SNIPPET: fieldsAndConstructors

  public CarSerializer( MoneySerializer moneySerializer, ExtraSerializer extraSerializer, ModelSerializer modelSerializer ) {
    super( "car", "http://thecompany.com/test/car", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );

    add( moneySerializer ).responsibleFor( Money.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );

    add( extraSerializer ).responsibleFor( Extra.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 5, 0 );

    add( modelSerializer ).responsibleFor( Model.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );

    //Verify the delegate mappings
    assert getDelegatesMappings().verify();
  }
  //END SNIPPET: fieldsAndConstructors


  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Car object ) throws IOException, XMLStreamException {
    SMOutputElement colorElement = serializeTo.addElement( serializeTo.getNamespace(), "color" );  //okay, should be a own serializer in real world...
    colorElement.addAttribute( "red", String.valueOf( object.getColor().getRed() ) );
    colorElement.addAttribute( "blue", String.valueOf( object.getColor().getBlue() ) );
    colorElement.addAttribute( "green", String.valueOf( object.getColor().getGreen() ) );

    serialize( object.getModel(), Model.class, serializeTo.addElement( serializeTo.getNamespace(), "model" ) );
    serialize( object.getBasePrice(), Money.class, serializeTo.addElement( serializeTo.getNamespace(), "basePrice" ) );


    //We could also at an additional tag called "extras". But I don't like that style... So here we go...
    serializeCollection( object.getExtras(), Extra.class, "extra", serializeTo );

    //The statement above does exactly the same as this loop:
    //    for ( Extra extra : object.getExtras() ) {
    //      serialize( Extra.class,  serializeTo.addElement( serializeTo.getNamespace(), "extra" ), extra );
    //    }
  }
  //END SNIPPET: serialize

  //START SNIPPET: deserialize

  @Override
  public Car deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    //We deserialize the color. This should be done in its own serializer in real world --> improved reusability and testability
    nextTag( deserializeFrom, "color" );
    int red = Integer.parseInt( deserializeFrom.getAttributeValue( null, "red" ) );
    int blue = Integer.parseInt( deserializeFrom.getAttributeValue( null, "blue" ) );
    int green = Integer.parseInt( deserializeFrom.getAttributeValue( null, "green" ) );
    Color color = new Color( red, green, blue );
    closeTag( deserializeFrom );

    nextTag( deserializeFrom, "model" );
    Model model = deserialize( Model.class, formatVersion, deserializeFrom );

    nextTag( deserializeFrom, "basePrice" );
    Money basePrice = deserialize( Money.class, formatVersion, deserializeFrom );

    //Now we visit all remaining children (should only be extras)
    List<? extends Extra> extras = deserializeCollection( deserializeFrom, Extra.class, formatVersion );

    return new Car( model, color, basePrice, extras );
  }
  //END SNIPPET: deserialize
}
