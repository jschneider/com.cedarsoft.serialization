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

import com.cedarsoft.serialization.SerializationException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SkipTypeInfoSerializer<T> extends AbstractJacksonSerializer<T> {
  @Nonnull
  private final AbstractJacksonSerializer<T> delegate;

  @Inject
  public SkipTypeInfoSerializer( @Nonnull AbstractJacksonSerializer<T> delegate ) {
    super( delegate.getType(), delegate.getFormatVersionRange() );
    if ( !delegate.isObjectType() ) {
      throw new SerializationException( SerializationException.Details.NOT_SUPPORTED_FOR_NON_OBJECT_TYPE, delegate.getClass().getName() );
    }

    this.delegate = delegate;
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    delegate.serialize( serializeTo, object, formatVersion );
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    return delegate.deserialize( deserializeFrom, formatVersion );
  }


  /**
   * @param generator the generator
   * @throws IOException if there is an io problem
   *
   * @noinspection RefusedBequest
   */
  @Override
  protected void writeTypeAndVersion( @Nonnull JsonGenerator generator ) throws IOException {
    //Do *not* write type and version!
  }

  /**
   * @param wrapper the parser
   * @param formatVersionOverride  the format version override
   * @throws IOException if there is an io problem
   * @noinspection RefusedBequest
   */
  @Nonnull
  @Override
  protected Version prepareDeserialization( @Nonnull JacksonParserWrapper wrapper, @Nullable Version formatVersionOverride ) throws IOException, SerializationException {
    //We do *not* read the type information and version here!
    wrapper.nextToken( JsonToken.START_OBJECT );
    return getFormatVersion();
  }
}
