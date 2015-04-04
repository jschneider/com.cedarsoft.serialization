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
import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Support class for serializing strategies.
 * <p>
 * It is necessary to register the strategies using {@link #addStrategy(SerializingStrategy)} and add the
 * necessary version mappings.
 *
 * @param <T> the type
 * @param <D> as defined in SerializingStrategy
 * @param <S> as defined in SerializingStrategy
 * @param <E> as defined in SerializingStrategy
 */
public class SerializingStrategySupport<T, S, D, E extends Throwable, O, I> {
  @Nonnull
  private final List<SerializingStrategy<? extends T, S, D, E, O, I>> strategies = new ArrayList<SerializingStrategy<? extends T, S, D, E, O, I>>();

  @Nonnull
  private final VersionMappings<SerializingStrategy<? extends T, S, D, E, O, I>> versionMappings;

  /**
   * Creates a new serializing strategy
   *
   * @param versionRange the format version range
   */
  public SerializingStrategySupport( @Nonnull VersionRange versionRange ) {
    versionMappings = new VersionMappings<SerializingStrategy<? extends T, S, D, E, O, I>>( versionRange );
  }

  /**
   * Returns the strategy for the given id.
   * Attention: The returned strategy is not able to serialize all types of T. Handle with care depending on the id!
   *
   * @param id the id
   * @return the strategy with that id
   *
   * @throws NotFoundException if not strategy could be found
   */
  @Nonnull
  public SerializingStrategy<? extends T, S, D, E, O, I> findStrategy( @Nonnull String id ) throws NotFoundException {
    for ( SerializingStrategy<? extends T, S, D, E, O, I> strategy : strategies ) {
      if ( strategy.getId().equals( id ) ) {
        return strategy;
      }
    }

    throw new NotFoundException( "No strategy found for id <" + id + ">" );
  }

  /**
   * Returns the first strategy that supports serialization for the given object
   *
   * @param object the object
   * @return the strategy that
   * @throws NotFoundException if the strategy could not be found for the given object
   */
  @Nonnull
  public <R extends T> SerializingStrategy<R, S, D, E, O, I> findStrategy( @Nonnull R object ) throws NotFoundException {
    for ( SerializingStrategy<? extends T, S, D, E, O, I> strategy : strategies ) {
      if ( strategy.supports( object ) ) {
        return ( SerializingStrategy<R, S, D, E, O, I> ) strategy;
      }
    }

    throw new NotFoundException( "No strategy found for object <" + object + ">" );
  }

  /**
   * Returns the strategies
   *
   * @return the strategies
   */
  @Nonnull
  public Collection<? extends SerializingStrategy<? extends T, S, D, E, O, I>> getStrategies() {
    return Collections.unmodifiableList( strategies );
  }

  @Nonnull
  public VersionMapping addStrategy( @Nonnull SerializingStrategy<? extends T, S, D, E, O, I> strategy ) {
    strategies.add( strategy );
    return versionMappings.add( strategy, strategy.getFormatVersionRange() );
  }

  @Nonnull
  public Version resolveVersion( @Nonnull SerializingStrategy<? extends T, S, D, E, O, I> key, @Nonnull Version version ) {
    return versionMappings.resolveVersion( key, version );
  }

  @Nonnull
  public VersionMappings<SerializingStrategy<? extends T, S, D, E, O, I>> getVersionMappings() {
    return versionMappings;
  }

  /**
   * Verifies the serializing strategy support
   *
   * @return the support
   */
  public boolean verify() {
    if ( strategies.isEmpty() ) {
      throw new SerializationException( SerializationException.Details.NO_STRATEGIES_AVAILABLE );
    }

    versionMappings.verify();
    return true;
  }
}
