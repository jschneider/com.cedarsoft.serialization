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

package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import com.cedarsoft.serialization.stax.CollectionsMapping;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializer extends AbstractStaxSerializer<User> {
  public UserSerializer( @Nonnull RoleSerializer roleSerializer, @Nonnull EmailSerializer emailSerializer ) {
    super( "user", "http://test/user", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    getDelegatesMappings().add( roleSerializer ).responsibleFor( Role.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( emailSerializer ).responsibleFor( Email.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @Nonnull XMLStreamWriter serializeTo, @Nonnull User object, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.writeStartElement( "name" );
    serializeTo.writeCharacters( object.getName() );
    serializeTo.writeEndElement();

    serializeCollection( object.getEmails(), Email.class, serializeTo, formatVersion );
    serializeCollection( object.getRoles(), Role.class, serializeTo, formatVersion );
  }

  @Nonnull
  @Override
  public User deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull final Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    String name = getChildText( deserializeFrom, "name" );

    List<Email> mails = new ArrayList<Email>();
    List<Role> roles = new ArrayList<Role>();
    deserializeCollections( deserializeFrom, formatVersion,
                            new CollectionsMapping()
                              .append( Email.class, mails, EmailSerializer.DEFAULT_ELEMENT_NAME )
                              .append( Role.class, roles, RoleSerializer.DEFAULT_ELEMENT_NAME )
    );

    return new User( name, mails, roles );
  }
}
