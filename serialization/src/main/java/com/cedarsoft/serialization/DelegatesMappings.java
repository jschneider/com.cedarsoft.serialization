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

import com.cedarsoft.UnsupportedVersionRangeException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Contains several delegates mappings
 *
 * @param <S> the object to serialize to (e.g. a dom element or stream)
 * @param <D> the object to deserialize from ((e.g. a dom element or stream)
 * @param <E> the exception that might be thrown
 */
public class DelegatesMappings<S, D, E extends Throwable> {
  @NotNull
  private final VersionRange versionRange;

  @NotNull
  private final Map<Class<?>, DelegateMapping> mappings = new HashMap<Class<?>, DelegateMapping>();
  @NotNull
  private final Map<Class<?>, Serializer<?>> serializers = new HashMap<Class<?>, Serializer<?>>();

  public DelegatesMappings( @NotNull VersionRange versionRange ) {
    this.versionRange = versionRange;
  }

  @NotNull
  public Map<? extends Class<?>, ? extends DelegateMapping> getMappings() {
    return Collections.unmodifiableMap( mappings );
  }

  @NotNull
  public <T> FluentFactory<T> add( @NotNull PluggableSerializer<? super T, S, D, E> serializer ) {
    return new FluentFactory( serializer );
  }

  @NotNull
  public <T> Version resolveVersion( @NotNull Class<? extends T> key, @NotNull Version version ) {
    return getMapping( key ).resolveVersion( version );
  }

  @NotNull
  public DelegateMapping getMapping( @NotNull Class<?> key ) {
    DelegateMapping mapping = mappings.get( key );
    if ( mapping == null ) {
      throw new IllegalArgumentException( "No mapping found for <" + key + ">" );
    }
    return mapping;
  }

  public <T> void serialize( @NotNull T object, @NotNull Class<T> type, @NotNull S outputElement ) throws E, IOException {
    PluggableSerializer<? super T, S, D, E> serializer = getSerializer( type );
    serializer.serialize( outputElement, object );
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
  public <T> T deserialize( @NotNull Class<T> type, @NotNull Version formatVersion, @NotNull D deserializeFrom ) throws E, IOException {
    PluggableSerializer<? super T, S, D, E> serializer = getSerializer( type );
    return type.cast( serializer.deserialize( deserializeFrom, resolveVersion( type, formatVersion ) ) );
  }

  /**
   * Verifies the mappings
   *
   * @return true if the verification has been successful. Throws an exception if not
   */
  public boolean verify() throws VersionException {
    SortedSet<Version> mappedVersions = getMappedVersions();

    if ( mappings.isEmpty() ) {
      throw new IllegalStateException( "No mappings available" );
    }

    for ( Map.Entry<Class<?>, DelegateMapping> entry : mappings.entrySet() ) {
      DelegateMapping mapping = entry.getValue();

      //Check for every entry whether the version ranges fit
      if ( !mapping.getVersionRange().equals( versionRange ) ) {
        throw new UnsupportedVersionRangeException( versionRange, mapping.getVersionRange(), "Invalid mapping for <" + entry.getKey().getName() + ">. " );
      }

      //Verify the mapping itself
      mapping.verify();
      mapping.verifyMappedVersions( mappedVersions );

      //Check the write version
      PluggableSerializer<?, S, D, E> serializer = getSerializer( entry.getKey() );
      if ( !serializer.getFormatVersion().equals( mapping.getDelegateWriteVersion() ) ) {
        throw new VersionMismatchException( mapping.getDelegateWriteVersion(), serializer.getFormatVersion(), "Invalid writing version for <" + entry.getKey().getName() + ">. " );
      }
    }

    return true;
  }

  /**
   * Returns the mapped versions
   *
   * @return a set with all mapped versions
   */
  @NotNull
  public SortedSet<Version> getMappedVersions() {
    SortedSet<Version> keyVersions = new TreeSet<Version>();
    for ( DelegateMapping mapping : getMappings().values() ) {
      keyVersions.add( mapping.getVersionRange().getMin() );
      keyVersions.add( mapping.getVersionRange().getMax() );

      for ( DelegateMapping.Entry entry : mapping.getEntries() ) {
        keyVersions.add( entry.getVersionRange().getMin() );
        keyVersions.add( entry.getVersionRange().getMax() );
      }
    }
    return keyVersions;
  }

  public class FluentFactory<T> {
    @NotNull
    private final PluggableSerializer<? super T, S, D, E> serializer;

    public FluentFactory( @NotNull PluggableSerializer<? super T, S, D, E> serializer ) {
      this.serializer = serializer;
    }

    @NotNull
    public DelegateMapping responsibleFor( @NotNull Class<? extends T> key ) {
      if ( mappings.containsKey( key ) ) {
        throw new IllegalArgumentException( "A serializer for the key <" + key + "> has still been added" );
      }

      DelegateMapping mapping = new DelegateMapping( versionRange, serializer.getFormatVersionRange() );
      mappings.put( key, mapping );
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
