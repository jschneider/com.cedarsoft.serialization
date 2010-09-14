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

package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class FileTypeSerializer extends AbstractJacksonSerializer<FileType> {
  @NonNls
  public static final String PROPERTY_EXTENSIONS = "extensions";
  @NonNls
  public static final String PROPERTY_ID = "id";
  @NonNls
  public static final String PROPERTY_DEPENDENTTYPE = "dependentType";
  @NonNls
  public static final String PROPERTY_CONTENTTYPE = "contentType";

  public FileTypeSerializer( @NotNull ExtensionSerializer extensionSerializer ) {
    super( "http://cedarsoft.com/file/file-type", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    add( extensionSerializer ).responsibleFor( Extension.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull FileType object, @NotNull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    serializeTo.writeArrayFieldStart( "daArray" );
    serializeTo.writeString( "daString" );

    serializeTo.writeStringField( PROPERTY_ID, object.getId() );
    serializeTo.writeBooleanField( PROPERTY_DEPENDENTTYPE, object.isDependentType() );
    serializeTo.writeStringField( PROPERTY_CONTENTTYPE, object.getContentType() );
    serializeArray( object.getExtensions(), Extension.class, PROPERTY_EXTENSIONS, serializeTo, formatVersion );
  }

  @NotNull
  @Override
  public FileType deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    nextField( deserializeFrom, PROPERTY_ID );
    String id = deserializeFrom.getText();
    nextField( deserializeFrom, PROPERTY_DEPENDENTTYPE );
    boolean dependentType = deserializeFrom.getBooleanValue();
    nextField( deserializeFrom, PROPERTY_CONTENTTYPE );
    String contentType = deserializeFrom.getText();
    List<? extends Extension> extensions = deserializeArray( Extension.class, PROPERTY_EXTENSIONS, deserializeFrom, formatVersion );
    closeObject( deserializeFrom );
    return new FileType( id, contentType, dependentType, extensions );
  }

}
