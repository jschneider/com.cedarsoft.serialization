package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for xml based serializers.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 */
public abstract class AbstractXmlSerializer<T, S, D, E extends Throwable> extends AbstractSerializer<T, S, D, E> {
  @NotNull
  @NonNls
  private final String defaultElementName;

  @NotNull
  @NonNls
  private final String nameSpaceUriBase;

  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name that is used for the root element.
   * @param formatVersionRange the version range. The max value is used when written.
   */
  @Deprecated
  protected AbstractXmlSerializer( @NotNull @NonNls String defaultElementName, @NotNull VersionRange formatVersionRange ) {
    this( defaultElementName, "http://" + defaultElementName, formatVersionRange );
  }
  
  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param nameSpaceUriBase   the base for the namespace uri
   * @param formatVersionRange the version range. The max value is used when written.
   */
  protected AbstractXmlSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.defaultElementName = defaultElementName;
    this.nameSpaceUriBase = nameSpaceUriBase;
  }

  @NotNull
  @NonNls
  protected String createNameSpaceUri( @NotNull Version formatVersion ) {
    return getNameSpaceUriBase() + "/" + formatVersion.format();
  }

  @NonNls
  @NotNull
  public String getNameSpaceUriBase() {
    return nameSpaceUriBase;
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

  /**
   * Parses the version from a namespace uri
   *
   * @param namespaceURI the namespace uri (the version has to be the last part split by "/"
   * @return the parsed version
   *
   * @throws IllegalArgumentException
   */
  @NotNull
  public static Version parseVersionFromNamespaceUri( @Nullable @NonNls String namespaceURI ) throws IllegalArgumentException, VersionException {
    if ( namespaceURI == null || namespaceURI.length() == 0 ) {
      throw new VersionException( "No version information found" );
    }

    String[] parts = namespaceURI.split( "/" );
    String last = parts[parts.length - 1];

    return Version.parse( last );
  }
}
