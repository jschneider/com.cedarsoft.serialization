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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Support class for serializing strategies
 *
 * @param <T> the type
 * @param <S> the serializing strategy type
 */
public class SerializingStrategySupport<T, S extends SerializingStrategy<? extends T, ?, ?, ?>> {
  @NotNull
  private final List<S> strategies = new ArrayList<S>();

  /**
   * Creates a new serializing strategy
   *
   * @param strategies the strategies
   */
  public SerializingStrategySupport( @NotNull Collection<? extends SerializingStrategy<? extends T, ?, ?, ?>> strategies ) {
    if ( strategies.isEmpty() ) {
      throw new IllegalArgumentException( "Need at least one strategy" );
    }
    this.strategies.addAll( ( Collection<? extends S> ) strategies );
  }

  /**
   * Returns the strategy for the given id
   *
   * @param id the id
   * @return the strategy with that id
   *
   * @throws NotFoundException if not strategy could be found
   */
  @NotNull
  public S findStrategy( @NotNull @NonNls String id ) throws NotFoundException {
    for ( S strategy : strategies ) {
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
   *
   * @throws NotFoundException
   */
  @NotNull
  public S findStrategy( @NotNull T object ) throws NotFoundException {
    for ( S strategy : strategies ) {
      if ( strategy.supports( object ) ) {
        return ( S ) strategy;
      }
    }

    throw new NotFoundException( "No strategy found for object <" + object + ">" );
  }

  /**
   * Returns the strategies
   *
   * @return the strategies
   */
  @NotNull
  public Collection<? extends S> getStrategies() {
    return Collections.unmodifiableList( strategies );
  }
}
