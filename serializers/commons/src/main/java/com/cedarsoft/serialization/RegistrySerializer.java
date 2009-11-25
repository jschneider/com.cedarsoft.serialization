package com.cedarsoft.serialization;

import com.cedarsoft.StillContainedException;
import com.cedarsoft.registry.DefaultRegistry;
import com.cedarsoft.registry.Registry;
import com.cedarsoft.registry.RegistryFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @param <T> the type
 * @param <R> the registry for the given type
 */
public class RegistrySerializer<T, R extends Registry<T>> {
  @NotNull
  private final Serializer<T> serializer;
  @NotNull
  private final SerializedObjectsAccess serializedObjectsAccess;
  @NotNull
  private final IdResolver<T> idResolver;

  @Nullable
  private final Comparator<T> comparator;

  /**
   * Creates a new registry serializer
   *
   * @param serializedObjectsAccess the serialized objects access
   * @param serializer              the serializer
   * @param idResolver              the id resolver
   */
  public RegistrySerializer( @NotNull SerializedObjectsAccess serializedObjectsAccess, @NotNull Serializer<T> serializer, @NotNull IdResolver<T> idResolver ) {
    this( serializedObjectsAccess, serializer, idResolver, null );
  }

  /**
   * Creates a new registry serializer
   *
   * @param serializedObjectsAccess the serialized objects access
   * @param serializer              the serializer
   * @param idResolver              the id resolver
   * @param comparator              the (optional) comparator
   */
  public RegistrySerializer( @NotNull SerializedObjectsAccess serializedObjectsAccess, @NotNull Serializer<T> serializer, @NotNull IdResolver<T> idResolver, @Nullable Comparator<T> comparator ) {
    this.serializer = serializer;
    this.serializedObjectsAccess = serializedObjectsAccess;
    this.idResolver = idResolver;
    this.comparator = comparator;
  }

  @NotNull
  public List<? extends T> deserialize() throws IOException {
    Set<? extends String> ids = serializedObjectsAccess.getStoredIds();

    List<T> objects = new ArrayList<T>();
    for ( String id : ids ) {
      objects.add( serializer.deserialize( serializedObjectsAccess.getInputStream( id ) ) );
    }

    //Sort the objects - if a comparator has been set
    if ( comparator != null ) {
      Collections.sort( objects, comparator );
    }

    return objects;
  }

  /**
   * Serializes an object
   *
   * @param object the object
   * @throws IOException
   */
  public void serialize( @NotNull T object ) throws StillContainedException, IOException {
    OutputStream out = serializedObjectsAccess.openOut( getId( object ) );
    try {
      serializer.serialize( object, out );
    } finally {
      out.close();
    }
  }

  /**
   * Creates a connected registry
   *
   * @param factory the factory used to create the registry
   * @return the connected registry
   *
   * @throws IOException
   */
  @NotNull
  public R createConnectedRegistry( @NotNull RegistryFactory<T, R> factory ) throws IOException {
    List<? extends T> objects = deserialize();
    R registry = factory.createRegistry( objects, new Comparator<T>() {
      @Override
      public int compare( T o1, T o2 ) {
        return getId( o1 ).compareTo( getId( o2 ) );
      }
    } );

    registry.addListener( new DefaultRegistry.Listener<T>() {
      @Override
      public void objectStored( @NotNull T object ) {
        try {
          serialize( object );
        } catch ( IOException e ) {
          throw new RuntimeException( e );
        }
      }
    } );
    return registry;
  }

  @NotNull
  @NonNls
  protected String getId( @NotNull T object ) {
    return idResolver.getId( object );
  }

  @NotNull
  public Serializer<T> getSerializer() {
    return serializer;
  }

  @NotNull
  public SerializedObjectsAccess getSerializedObjectsAccess() {
    return serializedObjectsAccess;
  }

  @NotNull
  public IdResolver<T> getIdResolver() {
    return idResolver;
  }

  @Nullable
  public Comparator<T> getComparator() {
    return comparator;
  }

  public interface IdResolver<T> {
    /**
     * Returns the id for the given object
     *
     * @param object the object
     * @return the id
     */
    @NotNull
    @NonNls
    String getId( @NotNull T object );
  }

}
