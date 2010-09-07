package com.cedarsoft.serialization.jackson;

import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 * @param <T> the type
 */
public abstract class AbstractJacksonSerializingStrategy<T> extends AbstractJacksonSerializer<T> implements JacksonSerializingStrategy<T> {
  @NotNull
  @NonNls
  private final String id;
  @NotNull
  private final Class<? extends T> supportedType;

  protected AbstractJacksonSerializingStrategy( @NotNull String id, @NonNls @NotNull String nameSpaceUriBase, @NotNull Class<? extends T> supportedType, @NotNull VersionRange formatVersionRange ) {
    super( nameSpaceUriBase, formatVersionRange );
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

  @NotNull
  public Class<? extends T> getSupportedType() {
    return supportedType;
  }
}
