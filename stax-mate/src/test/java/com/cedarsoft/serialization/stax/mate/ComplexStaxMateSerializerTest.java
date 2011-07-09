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

package com.cedarsoft.serialization.stax.mate;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest;
import org.codehaus.staxmate.out.SMOutputElement;
import org.junit.*;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class ComplexStaxMateSerializerTest extends AbstractXmlSerializerTest<String> {
  @Nonnull
  @Override
  protected AbstractStaxMateSerializer<String> getSerializer() {
    final AbstractStaxMateSerializer<String> stringSerializer = new AbstractStaxMateSerializer<String>( "asdf", "asdf", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull String object, @Nonnull Version formatVersion ) throws XMLStreamException {
        assert isVersionWritable( formatVersion );
        serializeTo.addCharacters( object );
      }

      @Override
      @Nonnull
      public String deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws XMLStreamException {
        assert isVersionReadable( formatVersion );
        deserializeFrom.next();
        String text = deserializeFrom.getText();
        closeTag( deserializeFrom );
        return text;
      }
    };

    return new AbstractStaxMateSerializer<String>( "aString", "asdf", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull String object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
        stringSerializer.serialize( serializeTo.addElement( serializeTo.getNamespace(), "sub" ), object, formatVersion );
        serializeTo.addElement( serializeTo.getNamespace(), "emptyChild" ).addCharacters( "" );
      }

      @Override
      @Nonnull
      public String deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
        assert isVersionReadable( formatVersion );
        nextTag( deserializeFrom, "sub" );
        String string = stringSerializer.deserialize( deserializeFrom, formatVersion );

        Assert.assertEquals( "", getChildText( deserializeFrom, "emptyChild" ) );
        closeTag( deserializeFrom );

        return string;
      }
    };
  }

  @Nonnull
  @Override
  protected String createObjectToSerialize() {
    return "asdf";
  }

  @Nonnull
  @Override
  protected String getExpectedSerialized() {
    return "<aString><sub>asdf</sub><emptyChild/></aString>";
  }
}