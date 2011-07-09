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

package com.cedarsoft.serialization.jackson;

import com.cedarsoft.version.VersionException;
import com.cedarsoft.serialization.InvalidNamespaceException;
import com.cedarsoft.serialization.PluggableSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @param <T> the type of object this serializer is able to (de)serialize
 */
public interface JacksonSerializer<T> extends PluggableSerializer<T, JsonGenerator, JsonParser, JsonProcessingException> {
  /**
   * Whether it is an object type. If true, the object braces are generated where necessary.
   *
   * @return whether it is an object type
   */
  boolean isObjectType();

  /**
   * Serializes the object to the given generator.
   * The serializer is responsible for writing start/close object/array brackets if necessary.
   * This method also writes the @type property.
   *
   * @param object    the object
   * @param generator the generator
   * @throws IOException
   */
  void serialize( @Nonnull T object, @Nonnull JsonGenerator generator ) throws IOException, JsonProcessingException;

  /**
   * Deserializes the object from the given parser.
   * This method deserializes the @type property.
   *
   * @param parser the parser
   * @return the deserialized object
   *
   * @throws IOException
   */
  @Nonnull
  T deserialize( @Nonnull JsonParser parser ) throws IOException, JsonProcessingException, InvalidTypeException;

  /**
   * Returns the type
   *
   * @return the type
   */
  @Nonnull
  String getType();

  /**
   * Verifies the name space
   *
   * @param namespace the name space
   * @throws InvalidNamespaceException
   * @throws VersionException
   */
  void verifyType( @Nullable String type ) throws InvalidTypeException;

}
