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
import com.cedarsoft.version.VersionMismatchException;
import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;
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
public class DelegatesMappings<S, D, E extends Throwable, O, I> {
  @Nonnull
  private final Map<Class<?>, Serializer<?,?,?>> serializers = new HashMap<Class<?>, Serializer<?,?,?>>();
  @Nonnull
  private final VersionMappings<Class<?>> versionMappings;

  public DelegatesMappings( @Nonnull VersionRange versionRange ) {
    versionMappings = new VersionMappings<Class<?>>( versionRange );
  }

  @Nonnull
  public <T> FluentFactory<T> add( @Nonnull PluggableSerializer<? super T, S, D, E, O, I> serializer ) {
    return new FluentFactory<T>( serializer );
  }

  public <T> void serialize( @Nonnull T object, @Nonnull Class<T> type, @Nonnull S outputElement, @Nonnull Version formatVersion ) throws E, IOException {
    PluggableSerializer<? super T, S, D, E,O, I> serializer = getSerializer( type );
    serializer.serialize( outputElement, object, versionMappings.resolveVersion( type, formatVersion ) );
  }

  @Nonnull
  public <T> PluggableSerializer<? super T, S, D, E,O, I> getSerializer( @Nonnull Class<T> type ) throws SerializationException{
    PluggableSerializer<? super T, S, D, E,O, I> serializer = ( PluggableSerializer<? super T, S, D, E, O, I> ) serializers.get( type );
    if ( serializer == null ) {
      throw new SerializationException( SerializationException.Details.NO_SERIALIZER_FOUND, type.getName());
    }
    return serializer;
  }

  @Nonnull
  public <T> T deserialize( @Nonnull Class<T> type, @Nonnull Version formatVersion, @Nonnull D deserializeFrom ) throws E, IOException {
    PluggableSerializer<? super T, S, D, E, O, I> serializer = getSerializer( type );
    return type.cast( serializer.deserialize( deserializeFrom, versionMappings.resolveVersion( type, formatVersion ) ) );
  }

  /**
   * Verifies the mappings
   *
   * @return true if the verification has been successful. Throws an exception if not
   */
  public boolean verify() throws VersionException {
    versionMappings.verify( new ToString<Class<?>>() {
      @Nonnull
      @Override
      public String convert( @Nonnull Class<?> object ) {
        return object.getName();
      }
    } );

    for ( Map.Entry<Class<?>, VersionMapping> entry : versionMappings.mappings.entrySet() ) {
      VersionMapping mapping = entry.getValue();

      //Check the write version
      PluggableSerializer<?, S, D, E, O, I> serializer = getSerializer( entry.getKey() );
      if ( !serializer.getFormatVersion().equals( mapping.getDelegateWriteVersion() ) ) {
        throw new VersionMismatchException( serializer.getFormatVersion(), mapping.getDelegateWriteVersion(), "Invalid serialization/output version for <" + entry.getKey().getName() + ">. " );
      }
    }

    return true;
  }

  @Nonnull
  public Map<? extends Class<?>, ? extends VersionMapping> getMappings() {
    return versionMappings.getMappings();
  }

  @Nonnull
  public <T> Version resolveVersion( @Nonnull Class<? extends T> key, @Nonnull Version version ) {
    return versionMappings.resolveVersion( key, version );
  }

  @Nonnull
  public VersionMapping getMapping( @Nonnull Class<?> key ) {
    return versionMappings.getMapping( key );
  }

  @Nonnull
  public SortedSet<Version> getMappedVersions() {
    return versionMappings.getMappedVersions();
  }

  @Nonnull
  public VersionRange getVersionRange() {
    return versionMappings.getVersionRange();
  }

  @Nonnull
  public VersionMappings<Class<?>> getVersionMappings() {
    return versionMappings;
  }

  public class FluentFactory<T> {
    @Nonnull
    private final PluggableSerializer<? super T, S, D, E, O, I> serializer;

    public FluentFactory( @Nonnull PluggableSerializer<? super T, S, D, E, O, I> serializer ) {
      this.serializer = serializer;
    }

    @Nonnull
    public VersionMapping responsibleFor( @Nonnull Class<? extends T> key ) {
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
