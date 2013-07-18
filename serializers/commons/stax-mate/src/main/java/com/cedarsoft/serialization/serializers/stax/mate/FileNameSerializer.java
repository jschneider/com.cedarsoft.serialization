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
import com.cedarsoft.file.BaseName;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * Serializer for file names
 */
public class FileNameSerializer extends AbstractStaxMateSerializer<FileName> {
  @Nonnull

  public static final String ELEMENT_EXTENSION = "extension";
  @Nonnull

  public static final String ELEMENT_BASE_NAME = "baseName";

  @Inject
  public FileNameSerializer( @Nonnull BaseNameSerializer baseNameSerializer, @Nonnull ExtensionSerializer extensionSerializer ) {
    super( "fileName", "http://www.cedarsoft.com/file/fileName", VersionRange.from( 1, 0, 0 ).to() );

    add( extensionSerializer ).responsibleFor( Extension.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
    ;

    add( baseNameSerializer ).responsibleFor( BaseName.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
    ;

    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull FileName object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );
    serialize( object.getBaseName(), BaseName.class, serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_BASE_NAME ), formatVersion );
    serialize( object.getExtension(), Extension.class, serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION ), formatVersion );
  }

  @Nonnull
  @Override
  public FileName deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    nextTag( deserializeFrom, ELEMENT_BASE_NAME );
    BaseName baseName = deserialize( BaseName.class, formatVersion, deserializeFrom );

    nextTag( deserializeFrom, ELEMENT_EXTENSION );
    Extension extension = deserialize( Extension.class, formatVersion, deserializeFrom );

    closeTag( deserializeFrom );

    return new FileName( baseName, extension );
  }
}
