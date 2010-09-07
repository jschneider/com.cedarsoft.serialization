/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 */
public abstract class AbstractNameSpaceBasedSerializer<T, S, D, E extends Throwable> extends AbstractSerializer<T, S, D, E> {
  @NotNull
  @NonNls
  protected final String nameSpaceUriBase;

  protected AbstractNameSpaceBasedSerializer( @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.nameSpaceUriBase = nameSpaceUriBase;
  }

  /**
   * Creates the namespace uri including the format version
   *
   * @param formatVersion the format version
   * @return the namespace uri
   */
  @NotNull
  @NonNls
  protected String createNameSpaceUri( @NotNull Version formatVersion ) {
    return getNameSpaceUriBase() + "/" + formatVersion.format();
  }

  /**
   * Returns the name space uri (including the version)
   *
   * @return the name space uri
   */
  @NotNull
  @NonNls
  public String getNameSpaceUri() {
    return createNameSpaceUri( getFormatVersion() );
  }

  /**
   * Returns the name space uri without the form at version
   *
   * @return the name space uri base
   */
  @NonNls
  @NotNull
  public String getNameSpaceUriBase() {
    return nameSpaceUriBase;
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

  /**
   * Verifies the namespace uri
   *
   * @param namespaceURI the namespace uri
   * @throws InvalidNamespaceException if the namespace is invalid
   * @throws VersionException          the if the version does not fit the expected range
   */
  protected void verifyNamespaceUri( @Nullable @NonNls String namespaceURI ) throws InvalidNamespaceException, VersionException {
    if ( namespaceURI == null || namespaceURI.trim().length() == 0 ) {
      throw new VersionException( "No version information available" );
    }
    String expectedBase = getNameSpaceUriBase();
    if ( !namespaceURI.startsWith( expectedBase ) ) {
      throw new InvalidNamespaceException( namespaceURI, expectedBase + "/$VERSION>" );
    }
  }

  /**
   * Parses the version from the namespace and verifies the namespace and version
   *
   * @param namespaceURI the namespace uri
   * @return the parsed and verified version
   *
   * @throws InvalidNamespaceException
   * @throws VersionException
   */
  @NotNull
  protected Version parseAndVerifyNameSpace( @Nullable @NonNls String namespaceURI ) throws InvalidNamespaceException, VersionException {
    //Verify the name space
    verifyNamespaceUri( namespaceURI );

    //Parse and verify the version
    Version formatVersion = parseVersionFromNamespaceUri( namespaceURI );
    verifyVersionReadable( formatVersion );
    return formatVersion;
  }
}
