/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.test.io2;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Model;
import com.cedarsoft.test.Money;
import com.cedarsoft.test.io.ModelSerializer;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

/**
 * This is an extended version of com.cedarsoft.test.io.CarSerializer.
 * <p>
 * It represents the next step of the evolution of CarSerializer.
 * This is an example for a refactoring that might happen, after the serializer has been
 * released (and shipped to thousands of customers creating millions of files).
 * <p>
 * Therefore this serializer is able to still read the old format.
 * Writing is only supported for the latest file.
 */
public class CarSerializer extends AbstractStaxMateSerializer<Car> {
  //START SNIPPET: fieldsAndConstructors

  public CarSerializer( MoneySerializer moneySerializer, ExtraSerializer extraSerializer, ModelSerializer modelSerializer ) {
    super( "car", "http://thecompany.com/test/car", VersionRange.from( 1, 0, 0 ).to( 1, 0, 1 ) );

    add( moneySerializer ).responsibleFor( Money.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
      .map( 1, 0, 1 ).toDelegateVersion( 1, 0, 1 )
    ;

    add( extraSerializer ).responsibleFor( Extra.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 5, 0 )
      .map( 1, 0, 1 ).toDelegateVersion( 1, 5, 1 )
    ;

    add( modelSerializer ).responsibleFor( Model.class )
      .map( 1, 0, 0 ).to( 1, 0, 1 ).toDelegateVersion( 1, 0, 0 );

    //Verify the delegate mappings
    assert getDelegatesMappings().verify();
  }
  //END SNIPPET: fieldsAndConstructors


  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Car object, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );

    SMOutputElement colorElement = serializeTo.addElement( serializeTo.getNamespace(), "color" );  //okay, should be a own serializer in real world...
    colorElement.addAttribute( "red", String.valueOf( object.getColor().getRed() ) );
    colorElement.addAttribute( "blue", String.valueOf( object.getColor().getBlue() ) );
    colorElement.addAttribute( "green", String.valueOf( object.getColor().getGreen() ) );

    serialize( object.getModel(), Model.class, serializeTo.addElement( serializeTo.getNamespace(), "model" ), formatVersion );
    serialize( object.getBasePrice(), Money.class, serializeTo.addElement( serializeTo.getNamespace(), "basePrice" ), formatVersion );


    //We could also at an additional tag called "extras". But I don't like that style... So here we go...
    serializeCollection( object.getExtras(), Extra.class, "extra", serializeTo, formatVersion );

    //The statement above does exactly the same as this loop:
    //    for ( Extra extra : object.getExtras() ) {
    //      serialize( Extra.class,  serializeTo.addElement( serializeTo.getNamespace(), "extra" ), extra );
    //    }
  }
  //END SNIPPET: serialize

  //START SNIPPET: deserialize

  @Override
  public Car deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
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
