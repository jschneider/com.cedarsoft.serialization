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
 */
public class VersionMappings {
  @NotNull
  protected final VersionRange versionRange;
  @NotNull
  protected final Map<Class<?>, VersionMapping> mappings = new HashMap<Class<?>, VersionMapping>();

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
  public Map<? extends Class<?>, ? extends VersionMapping> getMappings() {
    return Collections.unmodifiableMap( mappings );
  }

  /**
   * Resolves the version
   *
   * @param key     the key
   * @param version the version
   * @param <T>     the type
   * @return the mapped version
   */
  @NotNull
  public <T> Version resolveVersion( @NotNull Class<? extends T> key, @NotNull Version version ) {
    return getMapping( key ).resolveVersion( version );
  }

  /**
   * Returns the mapping for the given key
   *
   * @param key the key
   * @return the version mapping for the key
   */
  @NotNull
  public VersionMapping getMapping( @NotNull Class<?> key ) {
    VersionMapping mapping = mappings.get( key );
    if ( mapping == null ) {
      throw new IllegalArgumentException( "No mapping found for <" + key + ">" );
    }
    return mapping;
  }

  @NotNull
  protected VersionMapping addMapping( @NotNull Class<?> key, @NotNull VersionRange targetVersionRange ) {
    if ( mappings.containsKey( key ) ) {
      throw new IllegalArgumentException( "A serializer for the key <" + key + "> has still been added" );
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
}
