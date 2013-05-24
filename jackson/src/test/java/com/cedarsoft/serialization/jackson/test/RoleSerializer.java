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

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class RoleSerializer extends AbstractJacksonSerializer<Role> {

  public static final String PROPERTY_ID = "id";

  public static final String PROPERTY_DESCRIPTION = "description";

  public RoleSerializer() {
    super( "role", VersionRange.from( 1, 0, 0 ).to() );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Role object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeNumberField( PROPERTY_ID, object.getId() );
    serializeTo.writeStringField( PROPERTY_DESCRIPTION, object.getDescription() );
  }

  @Nonnull
  @Override
  public Role deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    int id = -1;
    String description = null;

    JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );

    while ( parser.nextToken() == JsonToken.FIELD_NAME ) {
      String currentName = parser.getCurrentName();

      if ( currentName.equals( PROPERTY_ID ) ) {
        parser.nextToken( JsonToken.VALUE_NUMBER_INT );
        id = parser.getIntValue();
        continue;
      }

      if ( currentName.equals( PROPERTY_DESCRIPTION ) ) {
        parser.nextToken( JsonToken.VALUE_STRING );
        description = parser.getText();
        continue;
      }

      throw new IllegalStateException( "Unexpected field reached <" + currentName + ">" );
    }


    //Verify
    parser.verifyDeserialized( id, PROPERTY_ID );
    parser.verifyDeserialized( description, PROPERTY_DESCRIPTION );
    assert description != null;


    parser.verifyCurrentToken( JsonToken.END_OBJECT );
    return new Role( id, description );
  }
}
