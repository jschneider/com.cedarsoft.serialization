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

package com.cedarsoft.serialization.jackson;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractNameSpaceBasedSerializer;
import com.cedarsoft.serialization.InvalidNamespaceException;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> the type
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractJacksonSerializer<T> extends AbstractNameSpaceBasedSerializer<T, JsonGenerator, JsonParser, JsonProcessingException> implements JacksonSerializer<T, JsonGenerator, JsonParser, JsonProcessingException> {
  @NonNls
  public static final String FIELD_NAME_DEFAULT_TEXT = "$";
  @NonNls
  public static final String PROPERTY_NS = "@ns";

  protected AbstractJacksonSerializer( @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();

    JsonGenerator generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );

    if ( isObjectType() ) {
      generator.writeStartObject();
      String nameSpace = getNameSpaceUri();
      generator.writeStringField( PROPERTY_NS, nameSpace );
    }

    serialize( generator, object, getFormatVersion() );

    if ( isObjectType() ) {
      generator.writeEndObject();
    }

    generator.close();
  }

  @NotNull
  @Override
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    try {
      JsonFactory jsonFactory = JacksonSupport.getJsonFactory();

      JsonParser parser = jsonFactory.createJsonParser( in );

      Version version;
      if ( isObjectType() ) {
        nextToken( parser, JsonToken.START_OBJECT );

        nextField( parser, PROPERTY_NS );
        version = parseAndVerifyNameSpace( parser.getText() );
      } else {
        parser.nextToken();
        version = getFormatVersion();
      }

      T deserialized = deserialize( parser, version );

      if ( isObjectType() ) {
        closeParserInObject( parser );
      } else {
        ensureParserClosed( parser );
      }

      return deserialized;
    } catch ( InvalidNamespaceException e ) {
      throw new IOException( "Could not parse due to " + e.getMessage(), e );
    }
  }

  public static void closeParserInObject( @NotNull JsonParser parser ) throws IOException {
    if ( parser.getCurrentToken() != JsonToken.END_OBJECT ) {
      throw new JsonParseException( "No consumed everything", parser.getCurrentLocation() );
    }
    ensureParserClosed( parser );
  }

  private static void ensureParserClosed( @NotNull JsonParser parser ) throws IOException {
    if ( parser.nextToken() != null ) {
      throw new JsonParseException( "No consumed everything", parser.getCurrentLocation() );
    }

    parser.close();
  }

  public static void nextField( @NotNull JsonParser parser, @NotNull @NonNls String fieldName ) throws IOException {
    nextToken( parser, JsonToken.FIELD_NAME );
    String currentName = parser.getCurrentName();

    if ( !fieldName.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + fieldName + "> but was <" + currentName + ">", parser.getCurrentLocation() );
    }

    parser.nextToken();
  }

  public static void nextToken( @NotNull JsonParser parser, @NotNull JsonToken expected ) throws IOException {
    JsonToken current = parser.nextToken();
    if ( current != expected ) {
      throw new JsonParseException( "Invalid token. Expected <" + expected + "> but got <" + current + ">", parser.getCurrentLocation() );
    }
  }

  public static void closeObject( @NotNull JsonParser deserializeFrom ) throws IOException {
    nextToken( deserializeFrom, JsonToken.END_OBJECT );
  }

  protected <T> void serializeArray( @NotNull Iterable<? extends T> elements, @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull JsonGenerator serializeTo, @NotNull Version formatVersion ) throws IOException {
    JacksonSerializer<? super T, JsonGenerator, JsonParser, JsonProcessingException> serializer = ( JacksonSerializer<? super T, JsonGenerator, JsonParser, JsonProcessingException> ) delegatesMappings.getSerializer( type );
    Version delegateVersion = delegatesMappings.getVersionMappings().resolveVersion( type, formatVersion );

    serializeTo.writeArrayFieldStart( propertyName );
    for ( T element : elements ) {
      if ( serializer.isObjectType() ) {
        serializeTo.writeStartObject();
      }

      serializer.serialize( serializeTo, element, delegateVersion );

      if ( serializer.isObjectType() ) {
        serializeTo.writeEndObject();
      }
    }
    serializeTo.writeEndArray();
  }

  @NotNull
  protected <T> List<? extends T> deserializeArray( @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException {
    nextField( deserializeFrom, propertyName );

    List<T> deserialized = new ArrayList<T>();
    while ( deserializeFrom.nextToken() != JsonToken.END_ARRAY ) {
      deserialized.add( deserialize( type, formatVersion, deserializeFrom ) );
    }
    return deserialized;
  }

  public <T> void serialize( @NotNull T object, @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull JsonGenerator deserializeTo, @NotNull Version formatVersion ) throws JsonProcessingException, IOException {
    deserializeTo.writeObjectFieldStart( propertyName );
    serialize( object, type, deserializeTo, formatVersion );
    deserializeTo.writeEndObject();
  }

  protected <T> T deserialize( @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull Version formatVersion, @NotNull JsonParser deserializeFrom ) throws IOException, JsonProcessingException {
    nextField( deserializeFrom, propertyName );
    return deserialize( type, formatVersion, deserializeFrom );
  }

  @Override
  public boolean isObjectType() {
    return true;
  }
}
