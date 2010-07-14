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

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.UnsupportedVersionRangeException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Contains several delegates mappings
 *
 * @param <S> the object to serialize to (e.g. a dom element or stream)
 * @param <D> the object to deserialize from ((e.g. a dom element or stream)
 * @param <E> the exception that might be thrown
 */
public class DelegatesMappings<S, D, E extends Throwable> {
  @NotNull
  private final Map<Class<?>, Serializer<?>> serializers = new HashMap<Class<?>, Serializer<?>>();

  @NotNull
  private final VersionMappings<Class<?>> versionMappings;

  public DelegatesMappings( @NotNull VersionRange versionRange ) {
    versionMappings = new VersionMappings<Class<?>>( versionRange );
  }

  @NotNull
  public <T> FluentFactory<T> add( @NotNull PluggableSerializer<? super T, S, D, E> serializer ) {
    return new FluentFactory<T>( serializer );
  }

  public <T> void serialize( @NotNull T object, @NotNull Class<T> type, @NotNull S outputElement, @NotNull Version formatVersion, @NotNull SerializationContext context ) throws E, IOException {
    PluggableSerializer<? super T, S, D, E> serializer = getSerializer( type );
    serializer.serialize( outputElement, object, versionMappings.resolveVersion( type, formatVersion ), context );
  }

  @NotNull
  public <T> PluggableSerializer<? super T, S, D, E> getSerializer( @NotNull Class<T> type ) {
    PluggableSerializer<? super T, S, D, E> serializer = ( PluggableSerializer<? super T, S, D, E> ) serializers.get( type );
    if ( serializer == null ) {
      throw new IllegalArgumentException( "No serializer found for <" + type.getName() + ">" );
    }
    return serializer;
  }

  @NotNull
  public <T> T deserialize( @NotNull Class<T> type, @NotNull Version formatVersion, @NotNull D deserializeFrom, @NotNull DeserializationContext context ) throws E, IOException {
    PluggableSerializer<? super T, S, D, E> serializer = getSerializer( type );
    return type.cast( serializer.deserialize( deserializeFrom, versionMappings.resolveVersion( type, formatVersion ), context ) );
  }

  /**
   * Verifies the mappings
   *
   * @return true if the verification has been successful. Throws an exception if not
   */
  public boolean verify() throws VersionException {
    SortedSet<Version> mappedVersions = versionMappings.getMappedVersions();

    if ( versionMappings.mappings.isEmpty() ) {
      throw new VersionException( "No mappings available" );
    }

    for ( Map.Entry<Class<?>, VersionMapping> entry : versionMappings.mappings.entrySet() ) {
      VersionMapping mapping = entry.getValue();

      //Check for every entry whether the version ranges fit
      if ( !mapping.getSourceVersionRange().equals( versionMappings.getVersionRange() ) ) {
        throw new UnsupportedVersionRangeException( versionMappings.getVersionRange(), mapping.getSourceVersionRange(), "Invalid mapping for <" + entry.getKey().getName() + ">. " );
      }

      //Verify the mapping itself
      try {
        mapping.verify();
        mapping.verifyMappedVersions( mappedVersions );
      } catch ( VersionMismatchException e ) {
        RuntimeException newException = new VersionMismatchException( e.getExpected(), e.getActual(), "Invalid mapping for <" + entry.getKey().getName() + ">: " + e.getMessage(), false );
        newException.setStackTrace( e.getStackTrace() );
        throw newException;
      } catch ( UnsupportedVersionException e ) {
        RuntimeException newException = new UnsupportedVersionException( e.getActual(), e.getSupportedRange(), "Invalid mapping for <" + entry.getKey().getName() + ">: " + e.getMessage(), false );
        newException.setStackTrace( e.getStackTrace() );
        throw newException;
      }

      //Check the write version
      PluggableSerializer<?, S, D, E> serializer = getSerializer( entry.getKey() );
      if ( !serializer.getFormatVersion().equals( mapping.getDelegateWriteVersion() ) ) {
        throw new VersionMismatchException( serializer.getFormatVersion(), mapping.getDelegateWriteVersion(), "Invalid serialization/output version for <" + entry.getKey().getName() + ">. " );
      }
    }

    return true;
  }

  @NotNull
  public Map<? extends Class<?>, ? extends VersionMapping> getMappings() {
    return versionMappings.getMappings();
  }

  @NotNull
  public <T> Version resolveVersion( @NotNull Class<? extends T> key, @NotNull Version version ) {
    return versionMappings.resolveVersion( key, version );
  }

  @NotNull
  public VersionMapping getMapping( @NotNull Class<?> key ) {
    return versionMappings.getMapping( key );
  }

  @NotNull
  public SortedSet<Version> getMappedVersions() {
    return versionMappings.getMappedVersions();
  }

  @NotNull
  public VersionRange getVersionRange() {
    return versionMappings.getVersionRange();
  }

  @NotNull
  public VersionMappings<Class<?>> getVersionMappings() {
    return versionMappings;
  }

  public class FluentFactory<T> {
    @NotNull
    private final PluggableSerializer<? super T, S, D, E> serializer;

    public FluentFactory( @NotNull PluggableSerializer<? super T, S, D, E> serializer ) {
      this.serializer = serializer;
    }

    @NotNull
    public VersionMapping responsibleFor( @NotNull Class<? extends T> key ) {
      VersionRange targetVersionRange = serializer.getFormatVersionRange();
      VersionMapping mapping = versionMappings.addMapping( key, targetVersionRange );

      serializers.put( key, serializer );
      return mapping;
    }
  }
}

/*
Ascii-Art sample:
              Window        Door        Other
----------------------------------------------
1.0.0         1.0.0         1.0.0       1.2.1
1.0.1           |             |         1.2.2
1.0.2           |             |         1.3.0
1.1.0           |             |         1.3.1
1.1.1           |             |         1.4.0
1.5.0         2.0.0           |           |
2.0.0           |             |         2.0.0
----------------------------------------------
2.0.0         2.0.0         1.0.0       2.0.0
*/
