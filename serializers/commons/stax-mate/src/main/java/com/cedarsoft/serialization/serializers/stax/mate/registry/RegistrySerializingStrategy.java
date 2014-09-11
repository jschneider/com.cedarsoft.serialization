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


import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;

/**
 * A registry serializing strategy
 *
 * @param <T> the type that is (de)serialized
 */
public interface RegistrySerializingStrategy<T> {
  /**
   * Deserialize the object
   *
   * @param id the id
   * @return the deserialized object
   *
   * @throws IOException if there is an io problem
   */
  @Nonnull
  T deserialize( @Nonnull String id ) throws IOException;

  /**
   * Serialize the object
   *
   * @param object the object to serialize
   * @param id     the id
   * @throws IOException if there is an io problem
   */
  void serialize( @Nonnull T object, @Nonnull String id ) throws IOException;

  void update( @Nonnull T object, @Nonnull String id ) throws IOException;

  void remove( @Nonnull T object, @Nonnull String id ) throws IOException;

  /**
   * Deserializes all
   *
   * @return the deserialized objects
   *
   * @throws IOException if there is an io problem
   */
  @Nonnull
  Collection<? extends T> deserialize() throws IOException;
}
