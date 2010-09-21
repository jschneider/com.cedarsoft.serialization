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
import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.SerializingStrategySupport;
import com.cedarsoft.serialization.VersionMapping;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

/**
 * @param <T> the type
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractDelegatingJacksonSerializer<T> extends AbstractJacksonSerializer<T> {
  @NotNull
  @NonNls
  public static final String PROPERTY_SUB_TYPE = "@subtype";
  @NotNull
  protected final SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException> serializingStrategySupport;

  protected AbstractDelegatingJacksonSerializer( @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( nameSpaceUriBase, formatVersionRange );
    this.serializingStrategySupport = new SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException>( formatVersionRange );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull T object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionWritable( formatVersion );

    SerializingStrategy<T, JsonGenerator, JsonParser, JsonProcessingException> strategy = serializingStrategySupport.findStrategy( object );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    serializeTo.writeStringField( PROPERTY_SUB_TYPE, strategy.getId() );

    strategy.serialize( serializeTo, object, resolvedVersion );
  }

  @NotNull
  @Override
  public T deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionReadable( formatVersion );

    nextFieldValue( deserializeFrom, PROPERTY_SUB_TYPE );
    String type = deserializeFrom.getText();

    if ( type == null ) {
      throw new JsonParseException( "Attribute" + PROPERTY_SUB_TYPE + " not found. Cannot find strategy.", deserializeFrom.getCurrentLocation() );
    }

    SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException> strategy = serializingStrategySupport.findStrategy( type );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    return strategy.deserialize( deserializeFrom, resolvedVersion );
  }

  @NotNull
  public Collection<? extends SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }

  @NotNull
  public VersionMapping addStrategy( @NotNull SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException> strategy ) {
    return serializingStrategySupport.addStrategy( strategy );
  }

  @NotNull
  public SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException> getSerializingStrategySupport() {
    return serializingStrategySupport;
  }
}
