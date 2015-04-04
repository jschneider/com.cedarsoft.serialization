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
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

/**
 * Attention: Does not yet work with StringValues! Use StringSerializer for those cases.
 *
 * Simply ignores the object/array
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class IgnoringSerializer extends AbstractJacksonSerializer<Void> {
  @Inject
  public IgnoringSerializer() {
    super( "ignoring", VersionRange.single( 0, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Void object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public Void deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    JsonToken inToken = deserializeFrom.nextToken();

    if ( isValueToken( inToken ) ) {
      deserializeFrom.nextToken();
      return null;
    }


    JsonToken outToken = findOutToken( inToken );

    int depth = 1;

    while ( depth > 0 ) {
      JsonToken next = deserializeFrom.nextToken();
      if ( next == inToken ) {
        depth++;
      }
      if ( next == outToken ) {
        depth--;
      }
    }

    //noinspection ConstantConditions
    return null;
  }

  private static boolean isValueToken( @Nonnull JsonToken inToken ) {
    return inToken.name().startsWith( "VALUE_" );
  }

  @Nonnull
  private static JsonToken findOutToken( @Nonnull JsonToken inToken ) {
    switch ( inToken ) {
      case START_OBJECT:
        return JsonToken.END_OBJECT;
      case START_ARRAY:
        return JsonToken.END_ARRAY;
    }

    throw new SerializationException( SerializationException.Details.INVALID_STATE, "No end token found for <" + inToken + ">" );
  }
}
