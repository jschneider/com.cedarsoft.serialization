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
import org.apache.commons.io.IOUtils;
import org.fest.reflect.core.Reflection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

/**
 * It is necessary to define at least one DataPoint
 * <pre>&#064;DataPoint<br/>public static final VersionEntry ENTRY1 = create(<br/> Version.valueOf( 1, 0, 0 ),<br/> &quot;&lt;json/&gt;&quot; );</pre>
 *
 * @param <T> the type
 */
public abstract class AbstractJsonVersionTest2<T> extends AbstractVersionTest2<T> {

  @NotNull
  protected static VersionEntry create( @NotNull Version version, @NotNull @NonNls String json ) {
    return new JsonVersionEntry( version, json );
  }

  @NotNull
  protected static VersionEntry create( @NotNull Version version, @NotNull @NonNls URL expected ) {
    try {
      return new JsonVersionEntry( version, IOUtils.toByteArray( expected.openStream() ) );
    } catch ( IOException e ) {
      throw new RuntimeException( e );
    }
  }

  public static class JsonVersionEntry implements VersionEntry {
    @NotNull
    private final Version version;
    @NotNull
    @NonNls
    private final byte[] json;

    public JsonVersionEntry( @NotNull Version version, @NotNull @NonNls String json ) {
      this( version, json.getBytes() );
    }

    public JsonVersionEntry( @NotNull Version version, @NotNull @NonNls byte[] json ) {
      this.version = version;
      this.json = json;
    }

    @NotNull
    @Override
    public Version getVersion() {
      return version;
    }

    @NotNull
    @Override
    public byte[] getSerialized( @NotNull Serializer<?> serializer ) throws Exception {
      boolean isObjectType = Reflection.method( "isObjectType" ).withReturnType( Boolean.TYPE ).in( serializer ).invoke();
      if ( isObjectType ) {
        return AbstractJsonSerializerTest2.addTypeInformation( AbstractJsonSerializerTest2.getType( serializer ), version, json ).getBytes();
      } else {
        return json;
      }
    }
  }
}
