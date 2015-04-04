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

package com.cedarsoft.serialization.serializers.jackson;

import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.file.FileTypeRegistry;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class FileTypeSerializer extends AbstractJacksonSerializer<FileType> {

  public static final String PROPERTY_EXTENSIONS = "extensions";

  public static final String PROPERTY_ID = "id";

  public static final String PROPERTY_DEPENDENT_TYPE = "dependentType";

  public static final String PROPERTY_CONTENT_TYPE = "contentType";

  @Inject
  public FileTypeSerializer( @Nonnull ExtensionSerializer extensionSerializer ) {
    super( "file-type", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    add( extensionSerializer ).responsibleFor( Extension.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull FileType object, @Nonnull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    serializeTo.writeStringField( PROPERTY_ID, object.getId() );
    serializeTo.writeBooleanField( PROPERTY_DEPENDENT_TYPE, object.isDependentType() );
    serializeTo.writeStringField( PROPERTY_CONTENT_TYPE, object.getContentType() );
    serializeArray( object.getExtensions(), Extension.class, PROPERTY_EXTENSIONS, serializeTo, formatVersion );
  }

  @Nonnull
  @Override
  public FileType deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    JacksonParserWrapper parserWrapper = new JacksonParserWrapper( deserializeFrom );
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName2 = parserWrapper.getCurrentName();

    if ( !PROPERTY_ID.equals( currentName2 ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_ID + "> but was <" + currentName2 + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String id = parserWrapper.getText();
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName1 = parserWrapper.getCurrentName();

    if ( !PROPERTY_DEPENDENT_TYPE.equals( currentName1 ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_DEPENDENT_TYPE + "> but was <" + currentName1 + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    boolean dependentType = parserWrapper.getBooleanValue();
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName = parserWrapper.getCurrentName();

    if ( !PROPERTY_CONTENT_TYPE.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_CONTENT_TYPE + "> but was <" + currentName + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String contentType = parserWrapper.getText();
    List<? extends Extension> extensions = deserializeArray( Extension.class, PROPERTY_EXTENSIONS, deserializeFrom, formatVersion );

    parserWrapper.nextToken( JsonToken.END_OBJECT );
    return new FileType( id, contentType, dependentType, extensions );
  }

  public static class Referenced extends AbstractJacksonSerializer<FileType> {
    @Nonnull
    private final FileTypeRegistry fileTypeRegistry;

    @Inject
    public Referenced( @Nonnull FileTypeRegistry fileTypeRegistry ) {
      super( "file-type", VersionRange.single( 1, 0, 0 ) );
      this.fileTypeRegistry = fileTypeRegistry;
    }

    @Override
    public boolean isObjectType() {
      return false;
    }

    @Override
    public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull FileType object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
      serializeTo.writeString( object.getId() );
    }

    @Nonnull
    @Override
    public FileType deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
      String id = deserializeFrom.getText();
      return fileTypeRegistry.valueOf( id );
    }
  }

}
