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
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Holds several {@link VersionMapping}s.
 *
 * @param <T> the type of the key
 */
public class VersionMappings<T> {
  @NotNull
  protected final VersionRange versionRange;
  @NotNull
  protected final Map<T, VersionMapping> mappings = new HashMap<T, VersionMapping>();

  /**
   * Creates a new version mappings
   *
   * @param versionRange the version range for the source
   */
  public VersionMappings( @NotNull VersionRange versionRange ) {
    this.versionRange = versionRange;
  }

  /**
   * Returns all available mappings
   *
   * @return the mappings
   */
  @NotNull
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
  @NotNull
  public Version resolveVersion( @NotNull T key, @NotNull Version version ) {
    return getMapping( key ).resolveVersion( version );
  }

  /**
   * Returns the mapping for the given key
   *
   * @param key the key
   * @return the version mapping for the key
   */
  @NotNull
  public VersionMapping getMapping( @NotNull T key ) {
    VersionMapping mapping = mappings.get( key );
    if ( mapping == null ) {
      throw new IllegalArgumentException( "No mapping found for <" + key + ">" );
    }
    return mapping;
  }

  @NotNull
  protected VersionMapping addMapping( @NotNull T key, @NotNull VersionRange targetVersionRange ) {
    if ( mappings.containsKey( key ) ) {
      throw new IllegalStateException( "An entry for the key <" + key + "> has still been added" );
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
  @NotNull
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

  @NotNull
  public VersionMapping add( @NotNull T key, @NotNull VersionRange targetVersionRange ) {
    return addMapping( key, targetVersionRange );
  }

  @NotNull
  public VersionRange getVersionRange() {
    return versionRange;
  }
}
