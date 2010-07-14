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

package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.inject.Inject;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class FileTypeSerializer extends AbstractStaxMateSerializer<FileType> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_DEPENDENT = "dependent";
  @NotNull
  @NonNls
  private static final String ELEMENT_ID = "id";
  @NotNull
  @NonNls
  private static final String ELEMENT_EXTENSION = "extension";
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_DEFAULT = "default";

  @Inject
  public FileTypeSerializer( @NotNull ExtensionSerializer extensionSerializer ) {
    super( "fileType", "http://www.cedarsoft.com/file/type", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );

    add( extensionSerializer ).responsibleFor( Extension.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
      ;

    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull FileType object, SerializationContext context ) throws IOException, XMLStreamException {
    serializeTo.addAttribute( ATTRIBUTE_DEPENDENT, String.valueOf( object.isDependentType() ) );
    serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_ID ).addCharacters( object.getId() );

    for ( Extension extension : object.getExtensions() ) {
      SMOutputElement extensionElement = serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION );

      if ( object.isDefaultExtension( extension ) ) {
        extensionElement.addAttribute( ATTRIBUTE_DEFAULT, String.valueOf( true ) );
      }

      serialize( extension, Extension.class, extensionElement );
    }
  }

  @NotNull
  @Override
  public FileType deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion, DeserializationContext context ) throws IOException, XMLStreamException {
    boolean dependent = Boolean.parseBoolean( deserializeFrom.getAttributeValue( null, ATTRIBUTE_DEPENDENT ) );
    String id = getChildText( deserializeFrom, ELEMENT_ID );

    List<? extends Extension> extensions = deserializeCollection( deserializeFrom, Extension.class, formatVersion );
    return new FileType( id, dependent, extensions );
  }
}
