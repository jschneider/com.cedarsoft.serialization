package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;

/**
 * Serializing strategy for neo4j
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNeo4jSerializingStrategy<T> extends AbstractNeo4jSerializer<T> implements Neo4jSerializingStrategy<T> {
  @Nonnull
  private final String id;
  @Nonnull
  private final Class<? extends T> supportedType;

  protected AbstractNeo4jSerializingStrategy( @Nonnull String id, @Nonnull String type, @Nonnull Class<? extends T> supportedType, @Nonnull VersionRange formatVersionRange ) {
    super( type, formatVersionRange );
    this.id = id;
    this.supportedType = supportedType;
  }

  @Override
  @Nonnull
  public String getId() {
    return id;
  }

  @Override
  public boolean supports( @Nonnull Object object ) {
    return supportedType.isAssignableFrom( object.getClass() );
  }

  @Nonnull
  public Class<? extends T> getSupportedType() {
    return supportedType;
  }
}


