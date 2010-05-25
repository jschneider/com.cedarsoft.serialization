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

package com.cedarsoft.serialization.registry;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Abstract base class for registry serializing strategy
 *
 * @param <T> the type
 * @param <O> the objects access
 */
public abstract class AbstractRegistrySerializingStrategy<T, O extends AbstractRegistrySerializingStrategy.ObjectsAccess> implements RegistrySerializingStrategy<T> {
  @NotNull
  protected final O objectsAccess;

  protected AbstractRegistrySerializingStrategy( @NotNull O objectsAccess ) {
    this.objectsAccess = objectsAccess;
  }

  @NotNull
  @Override
  public Collection<? extends T> deserialize() throws IOException {
    Set<? extends String> ids = objectsAccess.getIds();

    Collection<T> objects = new ArrayList<T>();
    for ( String id : ids ) {
      objects.add( deserialize( id ) );
    }

    return objects;
  }

  /**
   * Returns the object access
   *
   * @return the object access
   */
  @NotNull
  public O getObjectsAccess() {
    return objectsAccess;
  }

  /**
   */
  public interface ObjectsAccess {
    /**
     * Returns the ids
     *
     * @return the ids
     *
     * @throws IOException
     */
    @NotNull
    Set<? extends String> getIds() throws IOException;
  }
}