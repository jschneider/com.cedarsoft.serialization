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

import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializer extends AbstractJacksonSerializer<User> {

  public static final String PROPERTY_NAME = "id";

  public static final String PROPERTY_EMAILS = "emails";

  public static final String PROPERTY_ROLES = "roles";

  public UserSerializer( @Nonnull EmailSerializer emailSerializer, @Nonnull RoleSerializer roleSerializer, @Nonnull UserDetailsSerializer userDetailsSerializer ) {
    super( "user", VersionRange.from( 1, 0, 0 ).to() );

    getDelegatesMappings().add( emailSerializer ).responsibleFor( Email.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( roleSerializer ).responsibleFor( Role.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( userDetailsSerializer ).responsibleFor( UserDetails.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull User object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );

    serializeArray( object.getEmails(), Email.class, PROPERTY_EMAILS, serializeTo, formatVersion );
    serializeArray( object.getRoles(), Role.class, PROPERTY_ROLES, serializeTo, formatVersion );

    serialize( object.getUserDetails(), UserDetails.class, "userDetails", serializeTo, formatVersion );
    serialize( object.getSingleEmail(), Email.class, "singleEmail", serializeTo, formatVersion );
  }

  @Nonnull
  @Override
  public User deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    JacksonParserWrapper parserWrapper = new JacksonParserWrapper( deserializeFrom );
    parserWrapper.nextToken( JsonToken.FIELD_NAME );

    String currentName = parserWrapper.getCurrentName();

    if ( !PROPERTY_NAME.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + PROPERTY_NAME + "> but was <" + currentName + ">", parserWrapper.getCurrentLocation() );
    }
    parserWrapper.nextToken();
    String name = deserializeFrom.getText();

    List<? extends Email> mails = deserializeArray( Email.class, PROPERTY_EMAILS, deserializeFrom, formatVersion );
    List<? extends Role> roles = deserializeArray( Role.class, PROPERTY_ROLES, deserializeFrom, formatVersion );

    UserDetails userDetails = deserialize( UserDetails.class, "userDetails", formatVersion, deserializeFrom );
    Email singleEmail = deserialize( Email.class, "singleEmail", formatVersion, deserializeFrom );

    parserWrapper.nextToken();
    parserWrapper.verifyCurrentToken( JsonToken.END_OBJECT );
    return new User( name, mails, roles, singleEmail, userDetails );
  }
}
