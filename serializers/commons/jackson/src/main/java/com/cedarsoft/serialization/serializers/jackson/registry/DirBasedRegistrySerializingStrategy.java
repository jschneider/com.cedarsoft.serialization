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

package com.cedarsoft.serialization.serializers.jackson.registry;

import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Abstract base class for registry serializing strategies based on directory structures
 *
 * @param <T> the type
 */
public abstract class DirBasedRegistrySerializingStrategy<T> extends AbstractRegistrySerializingStrategy<T, DirBasedObjectsAccess> {
  protected DirBasedRegistrySerializingStrategy( @Nonnull DirBasedObjectsAccess objectsAccess ) {
    super( objectsAccess );
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull String id ) throws IOException {
    File dir = objectsAccess.getDirectory( id );
    return deserialize( id, dir );
  }

  /**
   * Deserialize the object from the given directory
   *
   * @param id  the id
   * @param dir the directory
   * @return the deserialized object
   *
   * @throws IOException if there is an io problem
   */
  @Nonnull
  protected abstract T deserialize( @Nonnull String id, @Nonnull File dir ) throws IOException;


  @Override
  public void serialize( @Nonnull T object, @Nonnull String id ) throws IOException {
    File dir = objectsAccess.addDirectory( id );
    serialize( object, id, dir );
  }

  @Override
  public void update( @Nonnull T object, @Nonnull String id ) throws IOException {
    File dir = objectsAccess.getDirectory( id );
    serialize( object, id, dir );
  }

  @Override
  public void remove( @Nonnull T object, @Nonnull String id ) throws IOException {
    File dir = objectsAccess.getDirectory( id );
    FileUtils.deleteDirectory( dir );
  }

  /**
   * Serialize the object to the given directory
   *
   * @param object the object to serialize
   * @param id     the id
   * @param dir    the directory
   * @throws IOException if there is an io problem
   */
  protected abstract void serialize( @Nonnull T object, @Nonnull String id, @Nonnull File dir ) throws IOException;

}
