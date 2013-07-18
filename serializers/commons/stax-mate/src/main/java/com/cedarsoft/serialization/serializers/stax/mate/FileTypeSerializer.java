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

package com.cedarsoft.serialization.serializers.stax.mate;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class FileTypeSerializer extends AbstractStaxMateSerializer<FileType> {
  @Nonnull

  private static final String ATTRIBUTE_DEPENDENT = "dependent";
  @Nonnull

  private static final String ELEMENT_ID = "id";
  @Nonnull

  private static final String ELEMENT_EXTENSION = "extension";
  @Nonnull

  private static final String ATTRIBUTE_DEFAULT = "default";
  @Nonnull

  public static final String ELEMENT_CONTENT_TYPE = "contentType";

  @Inject
  public FileTypeSerializer( @Nonnull ExtensionSerializer extensionSerializer ) {
    super( "fileType", "http://www.cedarsoft.com/file/type", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 1 ) ) );

    add( extensionSerializer ).responsibleFor( Extension.class )
      .map( 1, 0, 0 ).to( 1, 0, 1 ).toDelegateVersion( 1, 0, 0 )
    ;

    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull FileType object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );
    serializeTo.addAttribute( ATTRIBUTE_DEPENDENT, String.valueOf( object.isDependentType() ) );
    serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_ID ).addCharacters( object.getId() );
    serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_CONTENT_TYPE ).addCharacters( object.getContentType() );

    for ( Extension extension : object.getExtensions() ) {
      SMOutputElement extensionElement = serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION );

      if ( object.isDefaultExtension( extension ) ) {
        extensionElement.addAttribute( ATTRIBUTE_DEFAULT, String.valueOf( true ) );
      }

      serialize( extension, Extension.class, extensionElement, formatVersion );
    }
  }

  @Nonnull
  @Override
  public FileType deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    boolean dependent = Boolean.parseBoolean( deserializeFrom.getAttributeValue( null, ATTRIBUTE_DEPENDENT ) );
    String id = getChildText( deserializeFrom, ELEMENT_ID );

    String contentType;
    if ( formatVersion.equals( 1, 0, 0 ) ) {
      contentType = "application/unknown";
    } else {
      contentType = getChildText( deserializeFrom, ELEMENT_CONTENT_TYPE );
    }

    List<? extends Extension> extensions = deserializeCollection( deserializeFrom, Extension.class, formatVersion );
    return new FileType( id, contentType, dependent, extensions );
  }
}
