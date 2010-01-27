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
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains the mapping for delegating serializers
 */
public class DelegateMapping {
  @NotNull
  private final VersionRange versionRange;
  @NotNull
  private final VersionRange delegateVersionRange;
  @NotNull
  private final List<Entry> entries = new ArrayList<Entry>();

  public DelegateMapping( @NotNull VersionRange versionRange, @NotNull VersionRange delegateVersionRange ) {
    this.versionRange = versionRange;
    this.delegateVersionRange = delegateVersionRange;
  }

  @NotNull
  public Collection<? extends Entry> getEntries() {
    return Collections.unmodifiableCollection( entries );
  }

  @NotNull
  public VersionRange getVersionRange() {
    return versionRange;
  }

  @NotNull
  public VersionRange getDelegateVersionRange() {
    return delegateVersionRange;
  }

  @NotNull
  public FluentFactory map( @NotNull VersionRange range ) throws VersionException {
    return new FluentFactory( range );
  }

  @NotNull
  public FluentFactory map( @NotNull Version version ) throws VersionException {
    return new FluentFactory( VersionRange.from( version ).single() );
  }

  @NotNull
  public FluentFactory map( int major, int minor, int build ) throws VersionException {
    return map( Version.valueOf( major, minor, build ) );
  }

  /**
   * @param range           the version range
   * @param delegateVersion the delegate version
   */
  public void addMapping( @NotNull VersionRange range, @NotNull Version delegateVersion ) throws VersionException {
    if ( !versionRange.containsCompletely( range ) ) {
      throw new UnsupportedVersionRangeException( range, versionRange );
    }

    if ( !delegateVersionRange.contains( delegateVersion ) ) {
      throw new UnsupportedVersionException( delegateVersion, delegateVersionRange );
    }

    //Exists still a mapping?
    if ( containsMappingIn( range ) ) {
      throw new IllegalArgumentException( "The version range <" + range + "> still has been mapped" );
    }

    this.entries.add( new Entry( range, delegateVersion ) );
  }

  private boolean containsMappingIn( @NotNull VersionRange range ) {
    for ( Entry entry : entries ) {
      if ( entry.versionRange.overlaps( range ) ) {
        return true;
      }
    }
    return false;
  }

  @NotNull
  public Version resolveVersion( @NotNull Version version ) throws UnsupportedVersionException {
    for ( Entry entry : entries ) {
      if ( entry.versionRange.contains( version ) ) {
        return entry.delegateVersion;
      }
    }

    throw new UnsupportedVersionException( version );
  }

  public void verify() {
    if ( entries.isEmpty() ) {
      throw new IllegalStateException( "Contains no entries" );
    }

    {
      Entry first = entries.get( 0 );
      Version currentMin = first.getVersionRange().getMin();
      if ( !currentMin.equals( versionRange.getMin() ) ) {
        throw new IllegalStateException( "Invalid minimum version: <" + currentMin + ">, expected <" + versionRange.getMin() + ">" );
      }
    }

    {
      Entry last = entries.get( entries.size() - 1 );
      Version currentMax = last.getVersionRange().getMax();
      if ( !currentMax.equals( versionRange.getMax() ) ) {
        throw new IllegalStateException( "Invalid maximum version: <" + currentMax + ">, expected <" + versionRange.getMax() + ">" );
      }
    }
  }
  public void verifyMappedVersions( Iterable<? extends Version> mappedVersions ) {
    for ( Version mappedVersion : mappedVersions ) {
      resolveVersion( mappedVersion );
    }
  }

  public static class Entry {
    @NotNull
    private final VersionRange versionRange;
    @NotNull
    private final Version delegateVersion;

    Entry( @NotNull VersionRange versionRange, @NotNull Version delegateVersion ) {
      this.versionRange = versionRange;
      this.delegateVersion = delegateVersion;
    }

    @NotNull
    public VersionRange getVersionRange() {
      return versionRange;
    }

    @NotNull
    public Version getDelegateVersion() {
      return delegateVersion;
    }
  }

  public class FluentFactory {
    @NotNull
    private final VersionRange range;

    public FluentFactory( @NotNull VersionRange range ) {
      this.range = range;
    }

    @NotNull
    public DelegateMapping toDelegateVersion( int major, int minor, int build ) {
      return toDelegateVersion( Version.valueOf( major, minor, build ) );
    }

    @NotNull
    public DelegateMapping toDelegateVersion( @NotNull Version version ) {
      addMapping( range, version );
      return DelegateMapping.this;
    }

    @NotNull
    public FluentFactory to( int major, int minor, int build ) {
      return new FluentFactory( VersionRange.from( range.getMin() ).to( major, minor, build ) );
    }
  }
}
