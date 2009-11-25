package com.cedarsoft.serialization;

import com.cedarsoft.NotFoundException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @param <T> the type
 * @param <S> the serializing strategy type
 */
public class SerializingStrategySupport<T, S extends SerializingStrategy<? extends T, ?, ?, ?>> {
  @NotNull
  private final List<S> strategies = new ArrayList<S>();

  public SerializingStrategySupport( Collection<? extends SerializingStrategy<? extends T, ?, ?, ?>> strategies ) {
    this.strategies.addAll( ( Collection<? extends S> ) strategies );
  }

  @NotNull
  public S findStrategy( @NotNull @NonNls String type ) throws NotFoundException {
    for ( S strategy : strategies ) {
      if ( strategy.getId().equals( type ) ) {
        return ( S ) strategy;
      }
    }

    throw new NotFoundException();
  }

  @NotNull
  public S findStrategy( @NotNull T object ) throws NotFoundException {
    for ( S strategy : strategies ) {
      if ( strategy.supports( object ) ) {
        return ( S ) strategy;
      }
    }

    throw new NotFoundException( "No strategy found for object " + object );
  }

  @NotNull
  public Collection<? extends S> getStrategies() {
    return Collections.unmodifiableList( strategies );
  }
}
