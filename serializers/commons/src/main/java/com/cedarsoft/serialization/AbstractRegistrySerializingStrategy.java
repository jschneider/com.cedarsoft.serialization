package com.cedarsoft.serialization;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Abstract base class for registry serializing strategy
 *
 * @param <T> the type
 * @param <O> the objects access
 */
public abstract class AbstractRegistrySerializingStrategy<T, O extends AbstractRegistrySerializingStrategy.ObjectsAccess> implements RegistrySerializingStrategy<T> {
  @NotNull
  protected final O objectsAccess;

  protected AbstractRegistrySerializingStrategy( @NotNull O objectsAccess ) {
    this.objectsAccess = objectsAccess;
  }

  @NotNull
  @Override
  public Collection<? extends T> deserialize() throws IOException {
    Set<? extends String> ids = objectsAccess.getIds();

    Collection<T> objects = new ArrayList<T>();
    for ( String id : ids ) {
      objects.add( deserialize( id ) );
    }

    return objects;
  }

  /**
   */
  public interface ObjectsAccess {
    /**
     * Returns the ids
     *
     * @return the ids
     *
     * @throws IOException
     */
    @NotNull
    Set<? extends String> getIds() throws IOException;
  }
}
