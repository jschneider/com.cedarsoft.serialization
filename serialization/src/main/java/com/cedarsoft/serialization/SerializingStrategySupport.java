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
