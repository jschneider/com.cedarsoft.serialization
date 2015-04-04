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
import com.cedarsoft.license.CreativeCommonsLicense;
import com.cedarsoft.license.License;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;

public class LicenseSerializer extends AbstractJacksonSerializer<License> {

  public static final String PROPERTY_ID = "id";

  public static final String PROPERTY_NAME = "name";

  public static final String PROPERTY_URL = "url";

  public static final String SUB_TYPE_CC = "cc";

  @Inject
  public LicenseSerializer() {
    super( "license", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull License object, @Nonnull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    if ( object instanceof CreativeCommonsLicense ) {
      serializeTo.writeStringField( PROPERTY_SUB_TYPE, SUB_TYPE_CC );
    }

    //id
    serializeTo.writeStringField( PROPERTY_ID, object.getId() );
    //name
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );

    //URL
    serializeTo.writeFieldName( PROPERTY_URL );
    URL url = object.getUrl();
    if ( url == null ) {
      serializeTo.writeNull();
    } else {
      serializeTo.writeString( url.toString() );
    }
  }

  @Nonnull
  @Override
  public License deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    //If there is a subtype it *must* be cc
    JacksonParserWrapper parserWrapper = new JacksonParserWrapper( deserializeFrom );
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    if ( deserializeFrom.getCurrentName().equals( PROPERTY_SUB_TYPE ) ) {
      parserWrapper.nextToken();
      parserWrapper.verifyCurrentToken( JsonToken.VALUE_STRING );
      String subType = deserializeFrom.getText();

      if ( !subType.equals( SUB_TYPE_CC ) ) {
        throw new IllegalStateException( "Invalid sub type: " + subType );
      }
      parserWrapper.nextToken();
      parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
      String currentName = parserWrapper.getCurrentName();

      if ( !PROPERTY_ID.equals( currentName ) ) {
        throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_ID + "> but was <" + currentName + ">", parserWrapper.getCurrentLocation() );
      }
    }

    //id
    assert deserializeFrom.getCurrentName().equals( PROPERTY_ID );
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.VALUE_STRING );
    String id = deserializeFrom.getText();
    //name
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName1 = parserWrapper.getCurrentName();

    if ( !PROPERTY_NAME.equals( currentName1 ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_NAME + "> but was <" + currentName1 + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String name = deserializeFrom.getText();
    //url
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName = parserWrapper.getCurrentName();

    if ( !PROPERTY_URL.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_URL + "> but was <" + currentName + ">", parserWrapper.getCurrentLocation() );
    }
    JsonToken token = deserializeFrom.nextToken();
    @Nullable URL url;
    if ( token == JsonToken.VALUE_NULL ) {
      url = null;
    } else {
      url = new URL( deserializeFrom.getText() );
    }
    //Finally closing element
    parserWrapper.nextToken( JsonToken.END_OBJECT );

    //Constructing the deserialized object
    try {
      return License.get( id );
    } catch ( IllegalArgumentException ignore ) {
      return new License( id, name, url );
    }
  }
}
