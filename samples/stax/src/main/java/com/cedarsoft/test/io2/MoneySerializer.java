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

import com.cedarsoft.version.UnsupportedVersionException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import com.cedarsoft.test.Money;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * This is an extended version of money serializer.
 * <p>
 * It represents the next step of the evolution of MoneySerializer.
 * This is an example for a refactoring that might happen, after the serializer has been
 * released (and shipped to thousands of customers creating millions of files).
 * <p>
 * Therefore this serializer is able to still read the old format.
 * Writing is only supported for the latest file.
 */
public class MoneySerializer extends AbstractStaxSerializer<Money> {
  public MoneySerializer() {
    //This serializer supports an old version, too
    super( "money", "http://thecompany.com/test/money", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 1 ) ) );
  }

  @Override
  public void serialize( @Nonnull XMLStreamWriter serializeTo, @Nonnull Money object, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );
    serializeTo.writeAttribute( "cents", String.valueOf( object.getCents() ) );
    //We use an attribute - just because it is possible...
  }

  @Override
  public Money deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    //This serializer supports reading of an old format. Therefore we have to switch based on the format version.
    //This might be solved using the strategy pattern. But in most of the cases the format changes only in small portions.
    //So it seems easier to add just one if/else.
    //If a serializer evolves further a switch to the strategy pattern might be done. A simple map holding the strategies should do it.


    //The common case - current version
    if ( formatVersion.equals( new Version( 1, 0, 1 ) ) ) {
      int cents = Integer.parseInt( deserializeFrom.getAttributeValue( null, "cents" ) );

      //We have to close the tag! Every stax based serializer has to close its tag
      //getText does this automatically for us. But when only using attributes, we have to close it manually.
      closeTag( deserializeFrom );

      return new Money( cents );

      //The old format that does not use an attribute but text instead
    } else if ( formatVersion.equals( new Version( 1, 0, 0 ) ) ) {
      int cents = Integer.parseInt( getText( deserializeFrom ) );

      //We don't have to close the tag. The getText method does that for us
      return new Money( cents );

      //Whoo - something went terribly wrong
    } else {
      throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
    }
  }
}