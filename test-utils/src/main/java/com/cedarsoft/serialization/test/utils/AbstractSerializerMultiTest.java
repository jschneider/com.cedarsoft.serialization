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

package com.cedarsoft.serialization.test.utils;

import com.cedarsoft.serialization.Serializer;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Abstract class for serializer tests
 *
 * @param <T> the type of the serialized object
 * @deprecated use AbstractSerializerTest2 instead
 */
@Deprecated
public abstract class AbstractSerializerMultiTest<T> {
  @Test
  public void testSerializer() throws Exception {
    Serializer<T, OutputStream, InputStream> serializer = getSerializer();

    Iterable<? extends T> objectsToSerialize = createObjectsToSerialize();

    //Serialize
    List<? extends byte[]> serialized = serialize( serializer, objectsToSerialize );

    //Verify
    verifySerialized( serialized );


    List<T> deserialized = new ArrayList<T>();
    for ( byte[] currentSerialized : serialized ) {
      deserialized.add( serializer.deserialize( new ByteArrayInputStream( currentSerialized ) ) );
    }

    verifyDeserialized( deserialized );
  }

  @Nonnull
  private List<? extends byte[]> serialize( @Nonnull Serializer<T, OutputStream, InputStream> serializer, @Nonnull Iterable<? extends T> objectsToSerialize ) throws IOException {
    List<byte[]> serialized = new ArrayList<byte[]>();

    int index = 0;
    for ( T objectToSerialize : objectsToSerialize ) {
      try {
        serialized.add( serialize( serializer, objectToSerialize ) );
        index++;
      } catch ( IOException e ) {
        throw new IOException( "Serialization failed for (" + index + ") <" + objectsToSerialize + ">", e );
      }
    }
    return serialized;
  }

  @Nonnull
  protected byte[] serialize( @Nonnull Serializer<T, OutputStream, InputStream> serializer, @Nonnull T objectToSerialize ) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( objectToSerialize, out );
    return out.toByteArray();
  }

  /**
   * Returns the serializer
   *
   * @return the serializer
   * @throws Exception if there is any error
   */
  @Nonnull
  protected abstract Serializer<T, OutputStream, InputStream> getSerializer() throws Exception;

  /**
   * Verifies the serialized object
   *
   * @param serialized the serialized objects (within the same order)
   * @throws Exception if there is any error
   */
  protected abstract void verifySerialized( @Nonnull List<? extends byte[]> serialized ) throws Exception;

  /**
   * Creates the object to serialize
   *
   * @return the object to serialize
   * @throws Exception if there is any error
   */
  @Nonnull
  protected abstract Iterable<? extends T> createObjectsToSerialize() throws Exception;

  /**
   * Verifies the deserialized object.
   * The default implementation simply calls equals
   *
   * @param deserialized the deserialized object
   *                     @throws Exception if there is any error
   */
  protected void verifyDeserialized( @Nonnull List<? extends T> deserialized ) throws Exception {
    int index = 0;
    for ( T currentExpected : createObjectsToSerialize() ) {
      assertEquals( deserialized.get( index ), currentExpected );
      index++;
    }
  }
}