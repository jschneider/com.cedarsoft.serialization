package com.cedarsoft.serialization;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionMismatchException;
import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Abstract base class for all kinds of serializers.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractSerializer<T, S, D, E extends Throwable, O, I> implements PluggableSerializer<T, S, D, E, O, I> {
  @Nonnull
  protected final VersionRange formatVersionRange;
  @Nonnull
  protected final DelegatesMappings<S, D, E, O, I> delegatesMappings;

  /**
   * Creates a serializer.
   *
   * @param formatVersionRange the version range. The max value is used as format version when written.
   */
  protected AbstractSerializer( @Nonnull VersionRange formatVersionRange ) {
    this.formatVersionRange = formatVersionRange;
    this.delegatesMappings = new DelegatesMappings<S, D, E, O, I>( formatVersionRange );
  }

  @Override
  @Nonnull
  public Version getFormatVersion() {
    return formatVersionRange.getMax();
  }

  /**
   * Verifies the format version is supported
   *
   * @param formatVersion the format version
   */
  protected void verifyVersionReadable( @Nonnull Version formatVersion ) {
    if ( !isVersionReadable( formatVersion ) ) {
      throw new VersionMismatchException( getFormatVersionRange(), formatVersion );
    }
  }

  public boolean isVersionReadable( @Nonnull Version formatVersion ) {
    return getFormatVersionRange().contains( formatVersion );
  }

  /**
   * Verifies whether the format version is writable
   *
   * @param formatVersion the format version
   */
  protected void verifyVersionWritable( @Nonnull Version formatVersion ) {
    if ( !isVersionWritable( formatVersion ) ) {
      throw new VersionMismatchException( getFormatVersion(), formatVersion );
    }
  }

  public boolean isVersionWritable( @Nonnull Version formatVersion ) {
    return getFormatVersion().equals( formatVersion );
  }

  @Override
  @Nonnull
  public VersionRange getFormatVersionRange() {
    return formatVersionRange;
  }

  @Nonnull
  public DelegatesMappings<S, D, E, O, I> getDelegatesMappings() {
    return delegatesMappings;
  }

  @Nonnull
  public <T> DelegatesMappings<S, D, E, O, I>.FluentFactory<T> add( @Nonnull PluggableSerializer<? super T, S, D, E, O, I> pluggableSerializer ) {
    return delegatesMappings.add( pluggableSerializer );
  }

  public <T> void serialize( @Nonnull T object, @Nonnull Class<T> type, @Nonnull S deserializeTo, @Nonnull Version formatVersion ) throws E, IOException {
    delegatesMappings.serialize( object, type, deserializeTo, formatVersion );
  }

  @Nonnull
  public <T> PluggableSerializer<? super T, S, D, E, O, I> getSerializer( @Nonnull Class<T> type ) {
    return delegatesMappings.getSerializer( type );
  }

  @Nonnull
  public <T> T deserialize( @Nonnull Class<T> type, @Nonnull Version formatVersion, @Nonnull D deserializeFrom ) throws E, IOException {
    return delegatesMappings.deserialize( type, formatVersion, deserializeFrom );
  }

  /**
   * Helper method that can be used to ensure the right format version for each delegate.
   *
   * @param delegate              the delegate
   * @param expectedFormatVersion the expected format version
   */
  protected static void verifyDelegatingSerializerVersion( @Nonnull Serializer<?, ?, ?> delegate, @Nonnull Version expectedFormatVersion ) {
    Version actualVersion = delegate.getFormatVersion();
    if ( !actualVersion.equals( expectedFormatVersion ) ) {
      throw new SerializationException( SerializationException.Details.INVALID_VERSION, expectedFormatVersion, actualVersion );
    }
  }
}
