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


import com.cedarsoft.version.UnsupportedVersionException;
import com.cedarsoft.version.UnsupportedVersionRangeException;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionMismatchException;
import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Holds several VersionMappings.
 *
 * @param <T> the type of the key
 */
public class VersionMappings<T> {
  @Nonnull
  protected final VersionRange versionRange;
  @Nonnull
  protected final Map<T, VersionMapping> mappings = new HashMap<T, VersionMapping>();

  /**
   * Creates a new version mappings
   *
   * @param versionRange the version range for the source
   */
  public VersionMappings( @Nonnull VersionRange versionRange ) {
    this.versionRange = versionRange;
  }

  /**
   * Returns all available mappings
   *
   * @return the mappings
   */
  @Nonnull
  public Map<? extends T, ? extends VersionMapping> getMappings() {
    return Collections.unmodifiableMap( mappings );
  }

  /**
   * Resolves the version
   *
   * @param key     the key
   * @param version the version
   * @return the mapped version
   */
  @Nonnull
  public Version resolveVersion( @Nonnull T key, @Nonnull Version version ) {
    return getMapping( key ).resolveVersion( version );
  }

  /**
   * Returns the mapping for the given key
   *
   * @param key the key
   * @return the version mapping for the key
   */
  @Nonnull
  public VersionMapping getMapping( @Nonnull T key ) {
    VersionMapping mapping = mappings.get( key );
    if ( mapping == null ) {
      throw new SerializationException( SerializationException.Details.NO_MAPPING_FOUND, key );
    }
    return mapping;
  }

  @Nonnull
  protected VersionMapping addMapping( @Nonnull T key, @Nonnull VersionRange targetVersionRange ) {
    if ( mappings.containsKey( key ) ) {
      throw new IllegalArgumentException( "An entry for the key <" + key + "> has still been added" );
    }

    VersionMapping mapping = new VersionMapping( versionRange, targetVersionRange );
    mappings.put( key, mapping );
    return mapping;
  }

  /**
   * Returns the mapped versions
   *
   * @return a set with all mapped versions
   */
  @Nonnull
  public SortedSet<Version> getMappedVersions() {
    SortedSet<Version> keyVersions = new TreeSet<Version>();
    for ( VersionMapping mapping : getMappings().values() ) {
      keyVersions.add( mapping.getSourceVersionRange().getMin() );
      keyVersions.add( mapping.getSourceVersionRange().getMax() );

      for ( VersionMapping.Entry entry : mapping.getEntries() ) {
        keyVersions.add( entry.getVersionRange().getMin() );
        keyVersions.add( entry.getVersionRange().getMax() );
      }
    }
    return keyVersions;
  }

  @Nonnull
  public VersionMapping add( @Nonnull T key, @Nonnull VersionRange targetVersionRange ) {
    return addMapping( key, targetVersionRange );
  }

  @Nonnull
  public VersionRange getVersionRange() {
    return versionRange;
  }

  public boolean verify() throws VersionException {
    return verify( new ToString<T>() {
      @Nonnull
      @Override
      public String convert( @Nonnull T object ) {
        return object.toString();
      }
    } );
  }

  public boolean verify( @Nonnull ToString<T> toString ) throws VersionException {
    SortedSet<Version> mappedVersions = getMappedVersions();

    if ( mappings.isEmpty() ) {
      throw new VersionException( "No mappings available" );
    }

    for ( Map.Entry<T, VersionMapping> entry : mappings.entrySet() ) {
      VersionMapping mapping = entry.getValue();

      //Check for every entry whether the version ranges fit
      if ( !mapping.getSourceVersionRange().equals( getVersionRange() ) ) {
        throw new UnsupportedVersionRangeException( getVersionRange(), mapping.getSourceVersionRange(), "Invalid mapping for <" + toString.convert( entry.getKey() ) + ">. " );
      }

      //Verify the mapping itself
      try {
        mapping.verify();
        mapping.verifyMappedVersions( mappedVersions );
      } catch ( VersionMismatchException e ) {
        RuntimeException newException = new VersionMismatchException( e.getExpected(), e.getActual(), "Invalid mapping for <" + toString.convert( entry.getKey() ) + ">: " + e.getMessage(), false );
        newException.setStackTrace( e.getStackTrace() );
        throw newException;
      } catch ( UnsupportedVersionException e ) {
        RuntimeException newException = new UnsupportedVersionException( e.getActual(), e.getSupportedRange(), "Invalid mapping for <" + toString.convert( entry.getKey() ) + ">: " + e.getMessage(), false );
        newException.setStackTrace( e.getStackTrace() );
        throw newException;
      }
    }

    return true;
  }
}
