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
  private final Map<Serializer<?>, DelegateMapping> mappings = new HashMap<Serializer<?>, DelegateMapping>();

  public DelegatesMappings( @NotNull VersionRange versionRange ) {
    this.versionRange = versionRange;
  }

  @NotNull
  public <T> FluentFactory<T> add( @NotNull Serializer<? super T> serializer ) {
    return new FluentFactory( serializer );
  }

  @NotNull
  public <T> Version resolve( @NotNull Serializer<?> key, @NotNull Version version ) {
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
      mappings.put( serializer, mapping );
//      serializers.put( key, serializer );
      return mapping;
    }
  }
}
