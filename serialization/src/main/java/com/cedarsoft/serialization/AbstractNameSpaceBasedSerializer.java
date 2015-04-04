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

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNameSpaceBasedSerializer<T, S, D, E extends Throwable> extends AbstractStreamSerializer<T, S, D, E> {
  @Nonnull
  protected final String nameSpaceBase;

  protected AbstractNameSpaceBasedSerializer( @Nonnull String nameSpaceBase, @Nonnull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.nameSpaceBase = nameSpaceBase;
  }

  /**
   * Creates the namespace uri including the format version
   *
   * @param formatVersion the format version
   * @return the namespace uri
   */
  @Nonnull
  public String createNameSpace( @Nonnull Version formatVersion ) {
    return getNameSpaceBase() + "/" + formatVersion.format();
  }

  /**
   * Returns the name space uri (including the version)
   *
   * @return the name space uri
   */
  @Nonnull

  public String getNameSpace() {
    return createNameSpace( getFormatVersion() );
  }

  /**
   * Returns the name space uri without the form at version
   *
   * @return the name space uri base
   */

  @Nonnull
  public String getNameSpaceBase() {
    return nameSpaceBase;
  }

  /**
   * Parses the version from a namespace uri.
   * throws com.cedarsoft.version.VersionException if the namespace uri does not contain any version information   *
   * @param namespaceURI the namespace uri (the version has to be the last part split by "/"
   * @return the parsed version
   *
   */
  @Nonnull
  public static Version parseVersionFromNamespace( @Nullable String namespaceURI ) throws IllegalArgumentException, VersionException {
    if ( namespaceURI == null || namespaceURI.isEmpty() ) {
      throw new VersionException( "No version information found" );
    }

    int index = namespaceURI.lastIndexOf( '/' );
    String versionString = namespaceURI.substring( index + 1 );
    return Version.parse( versionString );
  }

  /**
   * Verifies the namespace uri
   * throws com.cedarsoft.version.VersionException          the if the version does not fit the expected range
   *
   * @param namespace the namespace uri
   * @throws SerializationException if the namespace is invalid
   */
  public void verifyNamespace( @Nullable String namespace ) throws SerializationException, VersionException {
    if ( namespace == null || namespace.trim().isEmpty() ) {
      throw new VersionException( "No version information available" );
    }
    String expectedBase = getNameSpaceBase();
    if ( !namespace.startsWith( expectedBase ) ) {
      throw new SerializationException( SerializationException.Details.INVALID_NAME_SPACE, expectedBase + "/" + getFormatVersion(), namespace );
    }
  }

  /**
   * Parses the version from the namespace and verifies the namespace and version
   *
   * @param namespaceURI the namespace uri
   * @return the parsed and verified version
   * @throws SerializationException if the namespace is invalid
   */
  @Nonnull
  public Version parseAndVerifyNameSpace( @Nullable String namespaceURI ) throws SerializationException, VersionException {
    //Verify the name space
    verifyNamespace( namespaceURI );

    //Parse and verify the version
    Version formatVersion = parseVersionFromNamespace( namespaceURI );
    verifyVersionReadable( formatVersion );
    return formatVersion;
  }
}
