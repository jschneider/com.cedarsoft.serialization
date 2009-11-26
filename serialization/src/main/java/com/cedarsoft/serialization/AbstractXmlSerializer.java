package com.cedarsoft.serialization;

import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for xml based serializers.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 */
public abstract class AbstractXmlSerializer<T, S, D, E extends Throwable> extends AbstractSerializer<T, S, D, E> {
  /**
   * The target of the processing instruction containing the version information
   */
  @NotNull
  @NonNls
  public static final String PI_TARGET_FORMAT = "format";

  @NotNull
  @NonNls
  private final String defaultElementName;

  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name that is used for the root element.
   * @param formatVersionRange the version range. The max value is used when written.
   */
  protected AbstractXmlSerializer( @NotNull @NonNls String defaultElementName, @NotNull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.defaultElementName = defaultElementName;
  }

  /**
   * Returns the default element name
   *
   * @return the default element name
   */
  @NotNull
  @NonNls
  protected String getDefaultElementName() {
    return defaultElementName;
  }
}
