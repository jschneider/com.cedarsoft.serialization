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
import com.cedarsoft.license.CreativeCommonsLicense;
import com.cedarsoft.license.License;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;

public class LicenseSerializer extends AbstractJacksonSerializer<License> {

  public static final String PROPERTY_ID = "id";

  public static final String PROPERTY_NAME = "name";

  public static final String PROPERTY_URL = "url";

  public static final String SUB_TYPE_CC = "cc";

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
    nextToken( deserializeFrom, JsonToken.FIELD_NAME );
    if ( deserializeFrom.getCurrentName().equals( PROPERTY_SUB_TYPE ) ) {
      nextToken( deserializeFrom, JsonToken.VALUE_STRING );
      String subType = deserializeFrom.getText();

      if ( !subType.equals( SUB_TYPE_CC ) ) {
        throw new IllegalStateException( "Invalid sub type: " + subType );
      }
      nextField( deserializeFrom, PROPERTY_ID );
    }

    //id
    assert deserializeFrom.getCurrentName().equals( PROPERTY_ID );
    nextToken( deserializeFrom, JsonToken.VALUE_STRING );
    String id = deserializeFrom.getText();
    //name
    nextFieldValue( deserializeFrom, PROPERTY_NAME );
    String name = deserializeFrom.getText();
    //url
    nextField( deserializeFrom, PROPERTY_URL );
    JsonToken token = deserializeFrom.nextToken();
    @Nullable URL url;
    if ( token == JsonToken.VALUE_NULL ) {
      url = null;
    } else {
      url = new URL( deserializeFrom.getText() );
    }
    //Finally closing element
    closeObject( deserializeFrom );

    //Constructing the deserialized object
    try {
      return License.get( id );
    } catch ( IllegalArgumentException ignore ) {
      return new License( id, name, url );
    }
  }
}
