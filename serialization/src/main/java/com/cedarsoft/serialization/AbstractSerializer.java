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
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import javax.annotation.Nonnull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Abstract base class for all kinds of serializers.
 *
 * @param <T> the type of object this serializer is able to (de)serialize
 * @param <S> the object to serialize to
 * @param <D> the object to deserialize from
 * @param <E> the exception that might be thrown
 */
public abstract class AbstractSerializer<T, S, D, E extends Throwable> implements PluggableSerializer<T, S, D, E> {
  @Nonnull
  private final VersionRange formatVersionRange;

  @Nonnull
  protected final DelegatesMappings<S, D, E> delegatesMappings;

  /**
   * Creates a serializer.
   *
   * @param formatVersionRange the version range. The max value is used as format version when written.
   */
  protected AbstractSerializer( @Nonnull VersionRange formatVersionRange ) {
    this.formatVersionRange = formatVersionRange;
    this.delegatesMappings = new DelegatesMappings<S, D, E>( formatVersionRange );
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

  /**
   * Helper method that serializes to a byte array
   *
   * @param object the object
   * @return the serialized object
   *
   * @throws IOException
   */
  @Nonnull
  public byte[] serializeToByteArray( @Nonnull T object ) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serialize( object, out );
    return out.toByteArray();
  }

  @Nonnull
  public DelegatesMappings<S, D, E> getDelegatesMappings() {
    return delegatesMappings;
  }

  // Delegate methods to the DelegatesMappings

  @Nonnull
  public <T> DelegatesMappings<S, D, E>.FluentFactory<T> add( @Nonnull PluggableSerializer<? super T, S, D, E> pluggableSerializer ) {
    return delegatesMappings.add( pluggableSerializer );
  }

  public <T> void serialize( @Nonnull T object, @Nonnull Class<T> type, @Nonnull S deserializeTo, @Nonnull Version formatVersion ) throws E, IOException {
    delegatesMappings.serialize( object, type, deserializeTo, formatVersion );
  }

  @Nonnull
  public <T> PluggableSerializer<? super T, S, D, E> getSerializer( @Nonnull Class<T> type ) {
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
  protected static void verifyDelegatingSerializerVersion( @Nonnull Serializer<?> delegate, @Nonnull Version expectedFormatVersion ) {
    Version actualVersion = delegate.getFormatVersion();
    if ( !actualVersion.equals( expectedFormatVersion ) ) {
      throw new IllegalArgumentException( "Invalid versions. Expected <" + expectedFormatVersion + "> but was <" + actualVersion + ">" );
    }
  }
}
