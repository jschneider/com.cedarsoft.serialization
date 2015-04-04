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
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

public class ExtensionSerializer extends AbstractJacksonSerializer<Extension> {

  public static final String PROPERTY_DELIMITER = "delimiter";

  public static final String PROPERTY_EXTENSION = "extension";

  @Inject
  public ExtensionSerializer() {
    super( "extension", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Extension object, @Nonnull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    //delimiter
    serializeTo.writeStringField( PROPERTY_DELIMITER, object.getDelimiter() );
    //extension
    serializeTo.writeStringField( PROPERTY_EXTENSION, object.getExtension() );
  }

  @Nonnull
  @Override
  public Extension deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    //delimiter
    JacksonParserWrapper parserWrapper = new JacksonParserWrapper( deserializeFrom );
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName1 = parserWrapper.getCurrentName();

    if ( !PROPERTY_DELIMITER.equals( currentName1 ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_DELIMITER + "> but was <" + currentName1 + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String delimiter = parserWrapper.getText();
    //extension
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName = parserWrapper.getCurrentName();

    if ( !PROPERTY_EXTENSION.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_EXTENSION + "> but was <" + currentName + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String extension = parserWrapper.getText();
    //Finally closing element
    parserWrapper.nextToken( JsonToken.END_OBJECT );
    //Constructing the deserialized object
    return new Extension( delimiter, extension );
  }

}
