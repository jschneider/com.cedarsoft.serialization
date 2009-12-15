package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Model;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CarSerializer extends AbstractStaxMateSerializer<Car> {
  //START SNIPPET: fieldsAndConstructors

  //We create constants for the expected versions for the delegates
  private static final Version VERSION_MONEY_SERIALIZER = new Version( 1, 0, 0 );
  private static final Version VERSION_MODEL_SERIALIZER = new Version( 1, 0, 0 );
  private static final Version VERSION_EXTRA_SERIALIZER = new Version( 1, 5, 0 );

  private final MoneySerializer moneySerializer;
  private final ExtraSerializer extraSerializer;
  private final ModelSerializer modelSerializer;

  public CarSerializer( MoneySerializer moneySerializer, ExtraSerializer extraSerializer, ModelSerializer modelSerializer ) {
    super( "car", new VersionRange( VERSION_MONEY_SERIALIZER, VERSION_MONEY_SERIALIZER ) );
    this.moneySerializer = moneySerializer;
    this.extraSerializer = extraSerializer;
    this.modelSerializer = modelSerializer;

    //Verify the versions for the other serializers
    verifyDelegatingSerializerVersion( moneySerializer, VERSION_MONEY_SERIALIZER );
    verifyDelegatingSerializerVersion( extraSerializer, VERSION_EXTRA_SERIALIZER );
    verifyDelegatingSerializerVersion( modelSerializer, VERSION_MODEL_SERIALIZER );
  }
  //END SNIPPET: fieldsAndConstructors


  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Car object ) throws IOException, XMLStreamException {
    SMOutputElement colorElement = serializeTo.addElement( "color" );  //okay, should be a own serializer in real world...
    colorElement.addAttribute( "red", String.valueOf( object.getColor().getRed() ) );
    colorElement.addAttribute( "blue", String.valueOf( object.getColor().getBlue() ) );
    colorElement.addAttribute( "green", String.valueOf( object.getColor().getGreen() ) );

    modelSerializer.serialize( serializeTo.addElement( "model" ), object.getModel() );
    moneySerializer.serialize( serializeTo.addElement( "basePrice" ), object.getBasePrice() );

    //We could also at an additional tag called "extras". But I don't like that style... So here we go...
    for ( Extra extra : object.getExtras() ) {
      extraSerializer.serialize( serializeTo.addElement( "extra" ), extra );
    }
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
    Model model = modelSerializer.deserialize( deserializeFrom, VERSION_MODEL_SERIALIZER );

    nextTag( deserializeFrom, "basePrice" );
    Money basePrice = moneySerializer.deserialize( deserializeFrom, VERSION_MONEY_SERIALIZER );

    //Now we visit all remaining children (should only be extras)
    final List<Extra> extras = new ArrayList<Extra>();
    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( XMLStreamReader deserializeFrom, @NonNls String tagName ) throws XMLStreamException, IOException {
        extras.add( extraSerializer.deserialize( deserializeFrom, VERSION_EXTRA_SERIALIZER ) );
      }
    } );

    return new Car( model, color, basePrice, extras );
  }
  //END SNIPPET: deserialize
}
