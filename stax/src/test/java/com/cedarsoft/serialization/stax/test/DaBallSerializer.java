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

package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class DaBallSerializer extends AbstractStaxSerializer<DaBall> {
  public DaBallSerializer() {
    super( "ball", "http://test/ball", VersionRange.from( 1, 0, 0 ).to( 1, 1, 0 ) );
  }

  public void registerElementsSerializer() {
    add( new ElementSerializer() ).responsibleFor( DaBall.Element.class )
      .map( 1, 1, 0 ).toDelegateVersion( 2, 0, 0 )
    ;
  }

  @Override
  public void serialize( @Nonnull XMLStreamWriter serializeTo, @Nonnull DaBall object, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.writeAttribute( "id", String.valueOf( object.getId() ) );

    serializeCollection( object.getElements(), DaBall.Element.class, "daElement", serializeTo, formatVersion );
  }

  @Nonnull
  @Override
  public DaBall deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    int id = Integer.parseInt( deserializeFrom.getAttributeValue( null, "id" ) );

    List<? extends DaBall.Element> elements = deserializeCollection( deserializeFrom, DaBall.Element.class, formatVersion );
    return new DaBall( id, elements );
  }

  public static class ElementSerializer extends AbstractStaxSerializer<DaBall.Element> {
    public ElementSerializer() {
      super( "element", "http://test/element", VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) );
    }

    @Override
    public void serialize( @Nonnull XMLStreamWriter serializeTo, @Nonnull DaBall.Element object, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      serializeTo.writeStartElement( "name1" );
      serializeTo.writeCharacters( object.getName() );
      serializeTo.writeEndElement();

      serializeTo.writeStartElement( "name2" );
      serializeTo.writeCharacters( object.getName() );
      serializeTo.writeEndElement();
    }

    @Nonnull
    @Override
    public DaBall.Element deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assertEquals( Version.valueOf( 2, 0, 0 ), formatVersion );

      String name2 = getChildText( deserializeFrom, "name1" );

      nextTag( deserializeFrom, "name2" );
      String name = getText( deserializeFrom );
      assertEquals( name, name2 );

      closeTag( deserializeFrom );

      return new DaBall.Element( name );
    }
  }
}
