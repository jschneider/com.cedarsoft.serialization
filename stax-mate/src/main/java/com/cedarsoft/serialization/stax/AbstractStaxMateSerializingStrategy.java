package com.cedarsoft.serialization.stax;

import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for serializing strategies using stax mate
 *
 * @param <T> the type
 */
public abstract class AbstractStaxMateSerializingStrategy<T> extends AbstractStaxMateSerializer<T> implements StaxMateSerializingStrategy<T> {
  @NotNull
  @NonNls
  private final String id;

  @NotNull
  private final Class<? extends T> supportedType;

  protected AbstractStaxMateSerializingStrategy( @NotNull String id, @NonNls @NotNull String nameSpaceUriBase, @NotNull Class<? extends T> supportedType, @NotNull VersionRange formatVersionRange ) {
    super( id, nameSpaceUriBase, formatVersionRange );
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
