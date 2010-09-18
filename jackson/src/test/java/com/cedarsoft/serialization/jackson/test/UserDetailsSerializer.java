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

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserDetailsSerializer extends AbstractJacksonSerializer<UserDetails> {
  @NonNls
  public static final String PROPERTY_LAST_LOGIN = "id";
  @NonNls
  public static final String PROPERTY_REGISTRATION_DATE = "registrationDate";
  @NonNls
  public static final String PROPERTY_PASSWORD_HASH = "passwordHash";

  public UserDetailsSerializer() {
    super( "http://cedarsoft.com/test/role", VersionRange.from( 1, 0, 0 ).to() );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull UserDetails object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeNumberField( PROPERTY_REGISTRATION_DATE, object.getRegistrationDate() );
    serializeTo.writeNumberField( PROPERTY_LAST_LOGIN, object.getLastLogin() );
    serializeTo.writeStringField( PROPERTY_PASSWORD_HASH, new String( Hex.encodeHex( object.getPasswordHash() ) ) );
  }

  @NotNull
  @Override
  public UserDetails deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    try {
      nextFieldValue( deserializeFrom, PROPERTY_REGISTRATION_DATE );
      long registrationDate = deserializeFrom.getLongValue();

      nextFieldValue( deserializeFrom, PROPERTY_LAST_LOGIN );
      long lastLogin = deserializeFrom.getLongValue();

      nextFieldValue( deserializeFrom, PROPERTY_PASSWORD_HASH );
      String passwordHash = deserializeFrom.getText();

      nextToken( deserializeFrom, JsonToken.END_OBJECT );
      return new UserDetails( registrationDate, lastLogin, Hex.decodeHex( passwordHash.toCharArray() ) );
    } catch ( DecoderException e ) {
      throw new RuntimeException( e );
    }
  }
}
