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

package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserDetailsSerializer extends AbstractJacksonSerializer<UserDetails> {

  public static final String PROPERTY_LAST_LOGIN = "id";

  public static final String PROPERTY_REGISTRATION_DATE = "registrationDate";

  public static final String PROPERTY_PASSWORD_HASH = "passwordHash";

  @Inject
  public UserDetailsSerializer() {
    super( "user-details", VersionRange.from( 1, 0, 0 ).to() );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull UserDetails object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeNumberField( PROPERTY_REGISTRATION_DATE, object.getRegistrationDate() );
    serializeTo.writeNumberField( PROPERTY_LAST_LOGIN, object.getLastLogin() );
    serializeTo.writeStringField( PROPERTY_PASSWORD_HASH, new String( Hex.encodeHex( object.getPasswordHash() ) ) );
  }

  @Nonnull
  @Override
  public UserDetails deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    long registrationDate = -1;
    long lastLogin = -1;
    String passwordHash = null;

    JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );

    while ( parser.nextToken() == JsonToken.FIELD_NAME ) {
      String currentName = parser.getCurrentName();

      if ( currentName.equals( PROPERTY_REGISTRATION_DATE ) ) {
        parser.nextToken( JsonToken.VALUE_NUMBER_INT );
        registrationDate = parser.getLongValue();
        continue;
      }

      if ( currentName.equals( PROPERTY_LAST_LOGIN ) ) {
        parser.nextToken( JsonToken.VALUE_NUMBER_INT );
        lastLogin = parser.getLongValue();
        continue;
      }

      if ( currentName.equals( PROPERTY_PASSWORD_HASH ) ) {
        parser.nextToken( JsonToken.VALUE_STRING );
        passwordHash = parser.getText();
        continue;
      }

      throw new IllegalStateException( "Unexpected field reached <" + currentName + ">" );
    }

    parser.verifyDeserialized( registrationDate, PROPERTY_REGISTRATION_DATE );
    parser.verifyDeserialized( lastLogin, PROPERTY_LAST_LOGIN );
    parser.verifyDeserialized( passwordHash, PROPERTY_PASSWORD_HASH );

    assert passwordHash != null;

    parser.ensureObjectClosed();

    try {
      return new UserDetails( registrationDate, lastLogin, Hex.decodeHex( passwordHash.toCharArray() ) );
    } catch ( DecoderException e ) {
      throw new RuntimeException( e );
    }
  }
}
