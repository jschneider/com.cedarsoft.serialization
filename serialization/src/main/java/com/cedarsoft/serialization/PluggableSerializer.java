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

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * This is a special serializer that is able to serialize an object to a given element.
 * The pluggable serializers can be used to delegate the serialization for child objects.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to (e.g. a dom element or stream)
 * @param <D> the object to deserialize from ((e.g. a dom element or stream)
 * @param <E> the exception that might be thrown
 */
public interface PluggableSerializer<T, S, D, E extends Throwable> extends Serializer<T> {
  /**
   * Serializes the object to the given element
   *
   * @param serializeTo   the serializeTo
   * @param object        the object
   * @param formatVersion the format version
   * @param context       the context  @throws IOException
   * @throws E
   */
  void serialize( @NotNull S serializeTo, @NotNull T object, Version formatVersion, @NotNull SerializationContext context ) throws IOException, E;

  /**
   * Deserializes the object from the given document
   *
   * @param deserializeFrom the deserializeFrom
   * @param formatVersion   the format version
   * @param context         the context
   * @return the deserialized object
   *
   * @throws VersionException
   * @throws IOException
   * @throws E
   */
  @NotNull
  T deserialize( @NotNull D deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException, VersionException, E;
}