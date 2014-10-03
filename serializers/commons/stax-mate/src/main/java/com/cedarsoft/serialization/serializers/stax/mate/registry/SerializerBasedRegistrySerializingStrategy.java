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

package com.cedarsoft.serialization.serializers.stax.mate.registry;

import com.cedarsoft.serialization.Serializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A serializing strategy based on a serializer.
 * This is the default strategy and used for most RegistrySerializers.
 *
 * @param <T> the type
 */
public class SerializerBasedRegistrySerializingStrategy<T> extends AbstractRegistrySerializingStrategy<T, StreamBasedObjectsAccess> {
  @Nonnull
  private final Serializer<T, OutputStream, InputStream> serializer;

  public SerializerBasedRegistrySerializingStrategy( @Nonnull StreamBasedObjectsAccess objectsAccess, @Nonnull Serializer<T, OutputStream, InputStream> serializer ) {
    super( objectsAccess );
    this.serializer = serializer;
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull String id ) throws IOException {
    return serializer.deserialize( objectsAccess.getInputStream( id ) );
  }

  @Override
  public void serialize( @Nonnull T object, @Nonnull String id ) throws IOException {
    OutputStream out = objectsAccess.openOut( id );
    try {
      serializer.serialize( object, out );
    } finally {
      out.close();
    }
  }

  @Override
  public void update( @Nonnull T object, @Nonnull String id ) throws IOException {
    OutputStream out = objectsAccess.openOutForUpdate( id );
    try {
      serializer.serialize( object, out );
    } finally {
      out.close();
    }
  }

  @Override
  public void remove( @Nonnull T object, @Nonnull String id ) throws IOException {
    objectsAccess.delete( id );
  }

  @Nonnull
  public Serializer<T, OutputStream, InputStream> getSerializer() {
    return serializer;
  }
}
