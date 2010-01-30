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

package com.cedarsoft.serialization;

import com.cedarsoft.provider.Provider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * A registry serializing strategy
 *
 * @param <T> the type that is (de)serialized
 * @param <P> the provider type
 */
public interface RegistrySerializingStrategy<T, P extends Provider<Set<? extends String>,IOException>> {
  /**
   * Deserialize the object
   *
   * @param id       the id
   * @param serializedObjectsProvider the serializedObjectsProvider
   * @return the deserialized object
   *
   * @throws IOException
   */
  @NotNull
  T deserialize( @NotNull @NonNls String id, @NotNull P serializedObjectsProvider ) throws IOException;

  /**
   * Serialize the object
   *
   * @param object   the object to serialize
   * @param id       the id
   * @param serializedObjectsProvider the serializedObjectsProvider
   * @throws IOException
   */
  void serialize( @NotNull T object, @NotNull @NonNls String id, @NotNull P serializedObjectsProvider ) throws IOException;
}
