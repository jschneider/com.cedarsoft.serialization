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

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializer extends AbstractJacksonSerializer<User> {

  public static final String PROPERTY_NAME = "id";

  public static final String PROPERTY_EMAILS = "emails";

  public static final String PROPERTY_ROLES = "roles";
  public static final String PROPERTY_USER_DETAILS = "userDetails";
  public static final String PROPERTY_SINGLE_EMAIL = "singleEmail";

  @Inject
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
    verifyVersionWritable( formatVersion );

    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );

    serializeArray( object.getEmails(), Email.class, PROPERTY_EMAILS, serializeTo, formatVersion );
    serializeArray( object.getRoles(), Role.class, PROPERTY_ROLES, serializeTo, formatVersion );

    serialize( object.getUserDetails(), UserDetails.class, PROPERTY_USER_DETAILS, serializeTo, formatVersion );
    serialize( object.getSingleEmail(), Email.class, PROPERTY_SINGLE_EMAIL, serializeTo, formatVersion );
  }

  @Nonnull
  @Override
  public User deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    verifyVersionWritable( formatVersion );

    List<? extends Email> mails = null;
    List<? extends Role> roles = null;
    String name = null;
    UserDetails userDetails = null;
    Email singleEmail = null;


    JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );
    while ( parser.nextToken() == JsonToken.FIELD_NAME ) {
      String currentName = parser.getCurrentName();

      if ( currentName.equals( PROPERTY_NAME ) ) {
        parser.nextToken( JsonToken.VALUE_STRING );
        name = deserializeFrom.getText();
        continue;
      }
      if ( currentName.equals( PROPERTY_EMAILS ) ) {
        parser.nextToken( JsonToken.START_ARRAY );
        mails = deserializeArray( Email.class, deserializeFrom, formatVersion );
        continue;
      }
      if ( currentName.equals( PROPERTY_ROLES ) ) {
        parser.nextToken( JsonToken.START_ARRAY );
        roles = deserializeArray( Role.class, deserializeFrom, formatVersion );
        continue;
      }
      if ( currentName.equals( PROPERTY_USER_DETAILS ) ) {
        parser.nextToken( JsonToken.START_OBJECT );
        userDetails = deserialize( UserDetails.class, formatVersion, deserializeFrom );
        continue;
      }
      if ( currentName.equals( PROPERTY_SINGLE_EMAIL ) ) {
        parser.nextToken( JsonToken.VALUE_STRING );
        singleEmail = deserialize( Email.class, formatVersion, deserializeFrom );
        continue;
      }

      throw new IllegalStateException( "Unexpected field reached <" + currentName + ">" );
    }

    //Verify deserialization
    parser.verifyDeserialized( mails, PROPERTY_EMAILS );
    parser.verifyDeserialized( roles, PROPERTY_ROLES );
    parser.verifyDeserialized( userDetails, PROPERTY_USER_DETAILS );
    parser.verifyDeserialized( singleEmail, PROPERTY_SINGLE_EMAIL );
    parser.verifyDeserialized( name, PROPERTY_NAME );
    assert mails != null;
    assert roles != null;
    assert name != null;
    assert userDetails != null;
    assert singleEmail != null;

    parser.ensureObjectClosed();

    return new User( name, mails, roles, singleEmail, userDetails );
  }
}
