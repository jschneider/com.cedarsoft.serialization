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
import com.cedarsoft.serialization.AbstractXmlSerializer;

import javax.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> the type
 * @deprecated use AbstractXmlVersionTest2 instead
 */
@Deprecated
public abstract class AbstractXmlVersionTest<T> extends AbstractVersionTest<T> {
  @Nonnull
  @Override
  protected final Map<? extends Version, ? extends byte[]> getSerialized() throws Exception {
    Map<Version, byte[]> serializedMap = new HashMap<Version, byte[]>();
    for ( Map.Entry<? extends Version, ? extends String> entry : getSerializedXml().entrySet() ) {
      byte[] xml = processXml( entry.getValue(), entry.getKey() );
      serializedMap.put( entry.getKey(), xml );
    }

    return serializedMap;
  }

  /**
   * Converts the xml string to a byte array used to deserialize.
   * This method automatically adds the namespace containing the version.
   *
   * @param xml     the xml
   * @param version the version
   * @return the byte array using the xml string
   */
  @Nonnull
  protected byte[] processXml( @Nonnull final String xml, @Nonnull Version version ) throws Exception {
    String nameSpace = ( (AbstractXmlSerializer<?, ?, ?, ?>) getSerializer() ).createNameSpace( version );
    return AbstractXmlSerializerTest2.addNameSpace( nameSpace, xml.getBytes() ).getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Returns a map containing the serialized xmls
   *
   * @return a map containing the serialized xmls
   */
  @Nonnull
  protected abstract Map<? extends Version, ? extends String> getSerializedXml();
}
