package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains several delegates mappings
 */
public class DelegatesMappings {
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
  public <T> FluentFactory<T> add( @NotNull Serializer<? super T> serializer ) {
    return new FluentFactory( serializer );
  }

  @NotNull
  public <T> Version resolveVersion( @NotNull Class<? extends T> key, @NotNull Version version ) {
    return mappings.get( key ).resolveVersion( version );
  }

  public class FluentFactory<T> {
    @NotNull
    private final Serializer<? super T> serializer;

    public FluentFactory( @NotNull Serializer<? super T> serializer ) {
      this.serializer = serializer;
    }

    @NotNull
    public DelegateMapping responsibleFor( @NotNull Class<? extends T> key ) {
      DelegateMapping mapping = new DelegateMapping( versionRange, serializer.getFormatVersionRange() );
      mappings.put( key, mapping );
      serializers.put( key, serializer );
      return mapping;
    }
  }
}
