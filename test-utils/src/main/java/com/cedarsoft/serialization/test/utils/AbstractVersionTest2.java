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

import com.cedarsoft.version.Version;
import com.cedarsoft.serialization.Serializer;
import org.junit.experimental.theories.*;
import org.junit.runner.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract test class for testing the support for multiple format versions
 * Attention: it is necessary to define at least one DataPoint:
 * <pre>&#064;DataPoint<br>public static final Entry&lt;?&gt; entry1 = create(<br> Version.valueOf( 1, 0, 0 ),<br> serializedAsByteArray; );</pre>
 *
 * @param <T> the type that is deserialized
 */
@RunWith( Theories.class )
public abstract class AbstractVersionTest2<T> {
  /**
   * This method checks old serialized objects
   *
   * @throws Exception if there is any error
   */
  @Theory
  public void testVersion( @Nonnull VersionEntry entry ) throws Exception {
    Serializer<T, OutputStream, InputStream> serializer = getSerializer();

    Version version = entry.getVersion();
    byte[] serialized = entry.getSerialized( serializer );

    T deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    verifyDeserialized( deserialized, version );
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
   * Verifies the deserialized object.
   *
   * @param deserialized the deserialized object
   * @param version      the version
   */
  protected abstract void verifyDeserialized( @Nonnull T deserialized, @Nonnull Version version ) throws Exception;
}
