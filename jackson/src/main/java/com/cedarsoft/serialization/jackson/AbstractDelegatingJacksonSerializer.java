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

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.SerializingStrategySupport;
import com.cedarsoft.serialization.VersionMapping;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @param <T> the type
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractDelegatingJacksonSerializer<T> extends AbstractJacksonSerializer<T> {
  @Nonnull
  protected final SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream> serializingStrategySupport;

  protected AbstractDelegatingJacksonSerializer( @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange ) {
    super( nameSpaceUriBase, formatVersionRange );
    this.serializingStrategySupport = new SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream>( formatVersionRange );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionWritable( formatVersion );

    SerializingStrategy<T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream> strategy = serializingStrategySupport.findStrategy( object );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    serializeTo.writeStringField( PROPERTY_SUB_TYPE, strategy.getId() );

    strategy.serialize( serializeTo, object, resolvedVersion );
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionReadable( formatVersion );

    JacksonParserWrapper parserWrapper = new JacksonParserWrapper( deserializeFrom );
    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.FIELD_NAME );
    String currentName = parserWrapper.getCurrentName();

    if ( !PROPERTY_SUB_TYPE.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_SUB_TYPE + "> but was <" + currentName + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String type = deserializeFrom.getText();

    if ( type == null ) {
      throw new JsonParseException( "Attribute" + PROPERTY_SUB_TYPE + " not found. Cannot find strategy.", deserializeFrom.getCurrentLocation() );
    }

    SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream> strategy = serializingStrategySupport.findStrategy( type );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    return strategy.deserialize( deserializeFrom, resolvedVersion );
  }

  @Nonnull
  public Collection<? extends SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }

  @Nonnull
  public VersionMapping addStrategy( @Nonnull SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream> strategy ) {
    return serializingStrategySupport.addStrategy( strategy );
  }

  @Nonnull
  public SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException, OutputStream, InputStream> getSerializingStrategySupport() {
    return serializingStrategySupport;
  }
}
