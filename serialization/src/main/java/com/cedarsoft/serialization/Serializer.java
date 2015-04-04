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

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Interface for all types of serializers.<br>
 * <p>
 * Each serializer is able to serialize an object to a given output stream.
 * <p>
 * A format version is supported for each serializer.
 *
 * @param <T> the type of the objects this serializer is able to (de)serialize
 * @param <O> the type of the object that is serialized to
 * @param <I> the type of the object that is serialized from
 */
public interface Serializer<T, O, I> {
  /**
   * Serializes the object to the given output object
   *
   * @param object the object to serialize
   * @param out    the out stream
   * @throws IOException if there is an io problem
   */
  void serialize( @Nonnull T object, @Nonnull O out ) throws IOException, SerializationException;

  /**
   * Deserializes the object from the input stream
   * throws com.cedarsoft.version.VersionException if any version related problem occurred
   * @param in the input object
   * @return the deserialized object
   *
   * @throws java.io.IOException if there is an io problem2
   */
  @Nonnull
  T deserialize( @Nonnull I in ) throws IOException, VersionException, SerializationException;

  /**
   * Returns the format version that is written.
   *
   * @return the format version that is written
   */
  @Nonnull
  Version getFormatVersion();

  /**
   * Returns the format version range this serializer supports when reading.
   *
   * @return the format version range that is supported
   */
  @Nonnull
  VersionRange getFormatVersionRange();
}