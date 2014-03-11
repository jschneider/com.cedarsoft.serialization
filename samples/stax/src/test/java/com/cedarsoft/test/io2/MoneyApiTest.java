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

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import com.cedarsoft.test.Money;
import org.junit.*;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 *
 */
public class MoneyApiTest extends AbstractXmlSerializerTest<MoneyApiTest.MyObject> {
  @Nonnull
  @Override
  protected String getExpectedSerialized() {
    return
      "<myObject>\n" +
        "  <money cents=\"203499\" />\n" +
        "</myObject>";
  }

  @Nonnull
  @Override
  protected StreamSerializer<MyObject> getSerializer() throws Exception {
    return new MyObjectSerializer( new com.cedarsoft.test.io2.MoneySerializer() );
  }

  @Nonnull
  @Override
  protected MyObject createObjectToSerialize() throws Exception {
    return new MyObject( new Money( 2034, 99 ) );
  }

  @Test
  public void testWrongMapping() {

  }

  public static class MyObjectSerializer extends AbstractStaxSerializer<MyObject> {
    public MyObjectSerializer( @Nonnull MoneySerializer moneySerializer ) {
      super( "myObject", "http://serialization.cedarsoft.com/test/myObject", VersionRange.from( Version.valueOf( 1, 4, 0 ) ).to( Version.valueOf( 1, 5, 0 ) ) );

      add( moneySerializer ).responsibleFor( Money.class )
        .map( 1, 4, 0 ).toDelegateVersion( 1, 0, 0 )
        .map( 1, 5, 0 ).toDelegateVersion( 1, 0, 1 )
      ;
    }

    @Override
    public void serialize( @Nonnull XMLStreamWriter serializeTo, @Nonnull MyObject object, Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.writeStartElement( "money" );
      serialize( object.getMoney(), Money.class, serializeTo, formatVersion );
      serializeTo.writeEndElement();
    }

    @Nonnull
    @Override
    public MyObject deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      nextTag( deserializeFrom, "money" );
      Money money = deserialize( Money.class, formatVersion, deserializeFrom );
      closeTag( deserializeFrom );

      return new MyObject( money );
    }
  }

  public static class MyObject {
    private final Money money;

    public MyObject( Money money ) {
      this.money = money;
    }

    public Money getMoney() {
      return money;
    }

    @Override
    public boolean equals( Object o ) {
      if ( this == o ) return true;
      if ( !( o instanceof MyObject ) ) return false;

      MyObject myObject = ( MyObject ) o;

      if ( money != null ? !money.equals( myObject.money ) : myObject.money != null ) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return money != null ? money.hashCode() : 0;
    }
  }
}
