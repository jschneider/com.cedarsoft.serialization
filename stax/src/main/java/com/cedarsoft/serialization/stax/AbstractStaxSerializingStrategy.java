package com.cedarsoft.serialization.stax;

import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for stax based serializing strategies
 *
 * @param <T> the type
 */
public abstract class AbstractStaxSerializingStrategy<T> extends AbstractStaxSerializer<T> implements StaxSerializingStrategy<T> {
  @NotNull
  @NonNls
  private final String id;

  @NotNull
  private final Class<? extends T> supportedType;

  /**
   * Creates a new strategy
   *
   * @param id                 the id
   * @param supportedType      the supported type
   * @param formatVersionRange the format version range
   */
  protected AbstractStaxSerializingStrategy( @NotNull String id, @NotNull Class<? extends T> supportedType, @NotNull VersionRange formatVersionRange ) {
    super( id, formatVersionRange );
    this.id = id;
    this.supportedType = supportedType;
  }

  @Override
  @NotNull
  public String getId() {
    return id;
  }

  @Override
  public boolean supports( @NotNull Object object ) {
    return supportedType.isAssignableFrom( object.getClass() );
  }
}