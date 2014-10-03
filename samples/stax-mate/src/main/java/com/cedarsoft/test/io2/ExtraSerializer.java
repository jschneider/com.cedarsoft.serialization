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
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * This is an extended version of com.cedarsoft.test.io.ExtraSerializer.
 * <p>
 * It represents the next step of the evolution of CarSerializer.
 * This is an example for a refactoring that might happen, after the serializer has been
 * released (and shipped to thousands of customers creating millions of files).
 * <p>
 * Therefore this serializer is able to still read the old format.
 * Writing is only supported for the latest file.
 */
public class ExtraSerializer extends AbstractStaxMateSerializer<Extra> {
  //START SNIPPET: fieldsAndConstructors

  public ExtraSerializer( MoneySerializer moneySerializer ) {
    super( "extra", "http://thecompany.com/test/extra", new VersionRange( new Version( 1, 5, 0 ), new Version( 1, 5, 1 ) ) );
    //We choose another version number. Maybe this is an old serializer that has been created within another project.

    add( moneySerializer ).responsibleFor( Money.class )
      .map( 1, 5, 0 ).toDelegateVersion( 1, 0, 0 )
      .map( 1, 5, 1 ).toDelegateVersion( 1, 0, 1 ) //this is the only line that has to be added! Everything else (below) stays the same!
    ;

    //Verify the delegate mappings
    //This is necessary, to ensure that the file format for the
    //object stays constant.
    //If someone changes the MoneySerializer (and increases the version number), this step
    //enforces us to take the necessary steps to handle that situation:
    //Either increase the version number of this serializer (recommended)
    //or handle the differences with some magic (may be necessary sometimes - but generally not recommended)
    assert getDelegatesMappings().verify();
  }
  //END SNIPPET: fieldsAndConstructors

  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Extra object, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );
    serializeTo.addElement( serializeTo.getNamespace(), "description" ).addCharacters( object.getDescription() );

    //We delegate the serialization of the price to the money serializer
    serialize( object.getPrice(), Money.class, serializeTo.addElement( serializeTo.getNamespace(), "price" ), formatVersion );
  }

  @Override
  public Extra deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
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
