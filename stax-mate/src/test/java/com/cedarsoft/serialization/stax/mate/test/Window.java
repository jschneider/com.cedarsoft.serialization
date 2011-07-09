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

package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.version.UnsupportedVersionException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class Window {
  private final double width;
  private final double height;
  private final String description;

  public Window( String description, double width, double height ) {
    this.width = width;
    this.height = height;
    this.description = description;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Window ) ) return false;

    Window window = ( Window ) o;

    if ( Double.compare( window.height, height ) != 0 ) return false;
    if ( Double.compare( window.width, width ) != 0 ) return false;
    if ( description != null ? !description.equals( window.description ) : window.description != null ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = width != +0.0d ? Double.doubleToLongBits( width ) : 0L;
    result = ( int ) ( temp ^ ( temp >>> 32 ) );
    temp = height != +0.0d ? Double.doubleToLongBits( height ) : 0L;
    result = 31 * result + ( int ) ( temp ^ ( temp >>> 32 ) );
    result = 31 * result + ( description != null ? description.hashCode() : 0 );
    return result;
  }

  public static class Serializer extends AbstractStaxMateSerializer<Window> {
    public Serializer() {
      super( "window", "window", new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Window object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.addAttribute( "width", String.valueOf( object.getWidth() ) );
      serializeTo.addAttribute( "height", String.valueOf( object.getHeight() ) );

      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );
    }

    @Nonnull
    @Override
    public Window deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
        double width = Double.parseDouble( deserializeFrom.getAttributeValue( null, "width" ) );
        double height = Double.parseDouble( deserializeFrom.getAttributeValue( null, "height" ) );

        String description = getChildText( deserializeFrom, "description" );

        closeTag( deserializeFrom );

        return new Window( description, width, height );
      } else if ( formatVersion.equals( Version.valueOf( 1, 0, 0 ) ) ) {
        double width = Double.parseDouble( getChildText( deserializeFrom, "width" ) );
        double height = Double.parseDouble( getChildText( deserializeFrom, "height" ) );

        String description = getChildText( deserializeFrom, "description" );

        closeTag( deserializeFrom );

        return new Window( description, width, height );
      } else {
        throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
      }
    }
  }
}
