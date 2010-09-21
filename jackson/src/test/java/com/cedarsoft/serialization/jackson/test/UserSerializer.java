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
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializer extends AbstractJacksonSerializer<User> {
  @NonNls
  public static final String PROPERTY_NAME = "id";
  @NonNls
  public static final String PROPERTY_EMAILS = "emails";
  @NonNls
  public static final String PROPERTY_ROLES = "roles";

  public UserSerializer( @NotNull EmailSerializer emailSerializer, @NotNull RoleSerializer roleSerializer, @NotNull UserDetailsSerializer userDetailsSerializer ) {
    super( "user", VersionRange.from( 1, 0, 0 ).to() );

    getDelegatesMappings().add( emailSerializer ).responsibleFor( Email.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( roleSerializer ).responsibleFor( Role.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( userDetailsSerializer ).responsibleFor( UserDetails.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull User object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );

    serializeArray( object.getEmails(), Email.class, PROPERTY_EMAILS, serializeTo, formatVersion );
    serializeArray( object.getRoles(), Role.class, PROPERTY_ROLES, serializeTo, formatVersion );

    serialize( object.getUserDetails(), UserDetails.class, "userDetails", serializeTo, formatVersion );
    serialize( object.getSingleEmail(), Email.class, "singleEmail", serializeTo, formatVersion );
  }

  @NotNull
  @Override
  public User deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    nextFieldValue( deserializeFrom, PROPERTY_NAME );
    String name = deserializeFrom.getText();

    List<? extends Email> mails = deserializeArray( Email.class, PROPERTY_EMAILS, deserializeFrom, formatVersion );
    List<? extends Role> roles = deserializeArray( Role.class, PROPERTY_ROLES, deserializeFrom, formatVersion );

    UserDetails userDetails = deserialize( UserDetails.class, "userDetails", formatVersion, deserializeFrom );
    Email singleEmail = deserialize( Email.class, "singleEmail", formatVersion, deserializeFrom );

    nextToken( deserializeFrom, JsonToken.END_OBJECT );
    return new User( name, mails, roles, singleEmail, userDetails );
  }
}
