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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;
import org.junit.runner.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Abstract base class for serializer tests.
 * @param <T> the type of domain object
 */
@RunWith( Theories.class )
public abstract class AbstractSerializerTest2<T> {
  @Theory
  public void testSerializer( @NotNull Entry<T> entry ) throws Exception {
    Serializer<T> serializer = getSerializer();

    //Serialize
    byte[] serialized = serialize( serializer, entry.object );

    //Verify
    verifySerialized( entry, serialized );

    verifyDeserialized( serializer.deserialize( new ByteArrayInputStream( serialized ) ), entry.object );
  }

  @NotNull
  protected byte[] serialize( @NotNull Serializer<T> serializer, @NotNull T objectToSerialize ) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( objectToSerialize, out );
    return out.toByteArray();
  }

  protected abstract void verifySerialized( @NotNull Entry<T> entry, @NotNull byte[] serialized ) throws Exception;

  /**
   * Returns the serializer
   *
   * @return the serializer
   */
  @NotNull
  protected abstract Serializer<T> getSerializer() throws Exception;

  /**
   * Verifies the deserialized object
   *
   * @param deserialized the deserialized object
   * @param original     the original
   */
  protected void verifyDeserialized( @NotNull T deserialized, @NotNull T original ) {
    assertEquals( deserialized, original );
    assertThat( deserialized, is( new ReflectionEquals( original ) ) );
  }

  protected static <T> Entry<T> create( @NotNull T object, @NotNull @NonNls String expected ) {
    return new Entry<T>( object, expected );
  }

  public static class Entry<T> {
    @NotNull
    private final T object;
    @NotNull
    @NonNls
    private final String expected;

    public Entry( @NotNull T object, @NotNull @NonNls String expected ) {
      this.object = object;
      this.expected = expected;
    }

    @NotNull
    public String getExpected() {
      return expected;
    }

    @NotNull
    public T getObject() {
      return object;
    }
  }
}
