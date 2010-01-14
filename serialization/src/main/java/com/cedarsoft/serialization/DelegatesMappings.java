package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
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

  public <T> void serialize( @NotNull Class<T> type, @NotNull S outputElement, @NotNull T object ) throws E, IOException {
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
   */
  public void verify() throws VersionException {
    SortedSet<Version> mappedVersions = getMappedVersions();

    for ( Map.Entry<Class<?>, DelegateMapping> entry : mappings.entrySet() ) {
      DelegateMapping mapping = entry.getValue();

      if ( !mapping.getVersionRange().equals( versionRange ) ) {
        throw new IllegalStateException( "Invalid mapping for <" + entry.getKey().getName() + ">. Expected to cover range " + versionRange + " but covered only <" + mapping.getVersionRange() + ">" );
      }

      mapping.verify();
      mapping.verifyMappedVersions( mappedVersions );
    }
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
