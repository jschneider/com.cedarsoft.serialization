package com.cedarsoft.serialization;

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.UnsupportedVersionRangeException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Contains the mapping for delegating serializers
 */
public class DelegateMapping {
  @NotNull
  private final VersionRange versionRange;
  @NotNull
  private final VersionRange delegateVersionRange;
  @NotNull
  private final Collection<Entry> entries = new ArrayList<Entry>();

  public DelegateMapping( @NotNull VersionRange versionRange, @NotNull VersionRange delegateVersionRange ) {
    this.versionRange = versionRange;
    this.delegateVersionRange = delegateVersionRange;
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

  private static class Entry {
    @NotNull
    private final VersionRange versionRange;
    @NotNull
    private final Version delegateVersion;

    Entry( @NotNull VersionRange versionRange, @NotNull Version delegateVersion ) {
      this.versionRange = versionRange;
      this.delegateVersion = delegateVersion;
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
