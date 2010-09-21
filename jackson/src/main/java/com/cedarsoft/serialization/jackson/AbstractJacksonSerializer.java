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
import com.cedarsoft.serialization.AbstractSerializer;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> the type
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractJacksonSerializer<T> extends AbstractSerializer<T, JsonGenerator, JsonParser, JsonProcessingException> implements JacksonSerializer<T> {
  @NonNls
  public static final String FIELD_NAME_DEFAULT_TEXT = "$";
  @NonNls
  public static final String PROPERTY_TYPE = "@type";
  @NonNls
  public static final String PROPERTY_VERSION = "@version";
  @NotNull
  @NonNls
  public static final String PROPERTY_SUB_TYPE = "@subtype";

  @NotNull
  @NonNls
  private final String type;

  protected AbstractJacksonSerializer( @NonNls @NotNull String type, @NotNull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.type = type;
  }

  @NotNull
  @Override
  public String getType() {
    return type;
  }

  @Override
  public void verifyType( @Nullable @NonNls String type ) throws InvalidTypeException {
    if ( !this.type.equals( type ) ) {
      throw new InvalidTypeException( type, this.type );
    }
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    JsonGenerator generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );

    serialize( object, generator );
    generator.close();
  }

  /**
   * Serializes the object to the given serializeTo.
   * <p/>
   * The serializer is responsible for writing start/close object/array brackets if necessary.
   * This method also writes the @type property.
   *
   * @param object    the object that is serialized
   * @param generator the serialize to object
   * @throws IOException
   */
  @Override
  public void serialize( @NotNull T object, @NotNull JsonGenerator generator ) throws IOException {
    if ( isObjectType() ) {
      generator.writeStartObject();
      generator.writeStringField( PROPERTY_TYPE, type );
      generator.writeStringField( PROPERTY_VERSION, getFormatVersion().format() );
    }

    serialize( generator, object, getFormatVersion() );

    if ( isObjectType() ) {
      generator.writeEndObject();
    }
  }

  @NotNull
  @Override
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    try {
      JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
      JsonParser parser = jsonFactory.createJsonParser( in );

      T deserialized = deserialize( parser );

      ensureParserClosed( parser );
      return deserialized;
    } catch ( InvalidTypeException e ) {
      throw new IOException( "Could not parse due to " + e.getMessage(), e );
    }
  }

  @Override
  @NotNull
  public T deserialize( @NotNull JsonParser parser ) throws IOException, JsonProcessingException, InvalidTypeException {
    Version version;
    if ( isObjectType() ) {
      nextToken( parser, JsonToken.START_OBJECT );

      nextFieldValue( parser, PROPERTY_TYPE );
      String readNs = parser.getText();
      verifyType( readNs );
      nextFieldValue( parser, PROPERTY_VERSION );
      version = Version.parse( parser.getText() );
      verifyVersionReadable( version );
    } else {
      parser.nextToken();
      version = getFormatVersion();
    }

    T deserialized = deserialize( parser, version );

    if ( isObjectType() ) {
      ensureObjectClosed( parser );
    }

    return deserialized;
  }

  @Deprecated
  public static void ensureParserClosedObject( @NotNull JsonParser parser ) throws IOException {
    ensureObjectClosed( parser );
    ensureParserClosed( parser );
  }

  public static void ensureObjectClosed( @NotNull JsonParser parser ) throws JsonParseException {
    if ( parser.getCurrentToken() != JsonToken.END_OBJECT ) {
      throw new JsonParseException( "No consumed everything " + parser.getCurrentToken(), parser.getCurrentLocation() );
    }
  }

  public static void ensureParserClosed( @NotNull JsonParser parser ) throws IOException {
    if ( parser.nextToken() != null ) {
      throw new JsonParseException( "No consumed everything " + parser.getCurrentToken(), parser.getCurrentLocation() );
    }

    parser.close();
  }

  /**
   * Verifies the next field has the given name and prepares for read (by calling parser.nextToken).
   *
   * @param parser    the parser
   * @param fieldName the field name
   * @throws IOException
   */
  public static void nextFieldValue( @NotNull JsonParser parser, @NotNull @NonNls String fieldName ) throws IOException {
    nextField( parser, fieldName );
    parser.nextToken();
  }

  /**
   * Verifies that the next field starts.
   * When the content of the field shall be accessed, it is necessary to call parser.nextToken() afterwards.
   *
   * @param parser    the parser
   * @param fieldName the field name
   * @throws IOException
   */
  public static void nextField( @NotNull JsonParser parser, @NotNull @NonNls String fieldName ) throws IOException {
    nextToken( parser, JsonToken.FIELD_NAME );
    String currentName = parser.getCurrentName();

    if ( !fieldName.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + fieldName + "> but was <" + currentName + ">", parser.getCurrentLocation() );
    }
  }

  public static void nextToken( @NotNull JsonParser parser, @NotNull JsonToken expected ) throws IOException {
    parser.nextToken();
    verifyCurrentToken( parser, expected );
  }

  public static void verifyCurrentToken( @NotNull JsonParser parser, @NotNull JsonToken expected ) throws JsonParseException {
    JsonToken current = parser.getCurrentToken();
    if ( current != expected ) {
      throw new JsonParseException( "Invalid token. Expected <" + expected + "> but got <" + current + ">", parser.getCurrentLocation() );
    }
  }

  public static void closeObject( @NotNull JsonParser deserializeFrom ) throws IOException {
    nextToken( deserializeFrom, JsonToken.END_OBJECT );
  }

  protected <T> void serializeArray( @NotNull Iterable<? extends T> elements, @NotNull Class<T> type, @NotNull JsonGenerator serializeTo, @NotNull Version formatVersion ) throws IOException {
    serializeArray( elements, type, null, serializeTo, formatVersion );
  }

  protected <T> void serializeArray( @NotNull Iterable<? extends T> elements, @NotNull Class<T> type, @Nullable @NonNls String propertyName, @NotNull JsonGenerator serializeTo, @NotNull Version formatVersion ) throws IOException {
    JacksonSerializer<? super T> serializer = getSerializer( type );
    Version delegateVersion = delegatesMappings.getVersionMappings().resolveVersion( type, formatVersion );

    if ( propertyName == null ) {
      serializeTo.writeStartArray();
    } else {
      serializeTo.writeArrayFieldStart( propertyName );
    }
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
  protected <T> List<? extends T> deserializeArray( @NotNull Class<T> type, @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException {
    return deserializeArray( type, null, deserializeFrom, formatVersion );
  }

  protected <T> List<? extends T> deserializeArray( @NotNull Class<T> type, @Nullable @NonNls String propertyName, @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException {
    if ( propertyName == null ) {
      assert deserializeFrom.getCurrentToken() == JsonToken.START_ARRAY;
    } else {
      nextFieldValue( deserializeFrom, propertyName );
    }

    List<T> deserialized = new ArrayList<T>();
    while ( deserializeFrom.nextToken() != JsonToken.END_ARRAY ) {
      deserialized.add( deserialize( type, formatVersion, deserializeFrom ) );
    }
    return deserialized;
  }

  public <T> void serialize( @Nullable T object, @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull JsonGenerator serializeTo, @NotNull Version formatVersion ) throws JsonProcessingException, IOException {
    serializeTo.writeFieldName( propertyName );

    //Fast exit if the value is null
    if ( object == null ) {
      serializeTo.writeNull();
      return;
    }

    JacksonSerializer<? super T> serializer = getSerializer( type );
    Version delegateVersion = delegatesMappings.getVersionMappings().resolveVersion( type, formatVersion );
    if ( serializer.isObjectType() ) {
      serializeTo.writeStartObject();
    }

    serializer.serialize( serializeTo, object, delegateVersion );

    if ( serializer.isObjectType() ) {
      serializeTo.writeEndObject();
    }
  }

  @Nullable
  protected <T> T deserializeNullable( @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull Version formatVersion, @NotNull JsonParser deserializeFrom ) throws IOException, JsonProcessingException {
    nextFieldValue( deserializeFrom, propertyName );

    if ( deserializeFrom.getCurrentToken() == JsonToken.VALUE_NULL ) {
      return null;
    }

    return deserialize( type, formatVersion, deserializeFrom );
  }

  @NotNull
  protected <T> T deserialize( @NotNull Class<T> type, @NotNull @NonNls String propertyName, @NotNull Version formatVersion, @NotNull JsonParser deserializeFrom ) throws IOException, JsonProcessingException {
    nextFieldValue( deserializeFrom, propertyName );
    return deserialize( type, formatVersion, deserializeFrom );
  }

  @NotNull
  @Override
  public <T> JacksonSerializer<? super T> getSerializer( @NotNull Class<T> type ) {
    return ( JacksonSerializer<? super T> ) super.getSerializer( type );
  }

  @Override
  public boolean isObjectType() {
    return true;
  }
}
