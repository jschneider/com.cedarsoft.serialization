package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.SerializingStrategySupport;
import com.cedarsoft.serialization.VersionMapping;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class AbstractDelegatingNeo4jSerializer<T> extends AbstractNeo4jSerializer<T> {
  @Nonnull
  protected final SerializingStrategySupport<T, Node, Node, IOException, Node, Node> serializingStrategySupport;

  protected AbstractDelegatingNeo4jSerializer( @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange ) {
    super( nameSpaceUriBase, formatVersionRange );
    this.serializingStrategySupport = new SerializingStrategySupport<T, Node, Node, IOException, Node, Node>( formatVersionRange );
  }

  @Override
  protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException {
    assert isVersionWritable( formatVersion );

    SerializingStrategy<T, Node, Node, IOException, Node, Node> strategy = serializingStrategySupport.findStrategy( object );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );

    serializeTo.setProperty( PROPERTY_SUB_TYPE, strategy.getId() );

    strategy.serialize( serializeTo, object, resolvedVersion );
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
    assert isVersionReadable( formatVersion );

    try {
      @Nonnull String subType = ( String ) deserializeFrom.getProperty( PROPERTY_SUB_TYPE );
      SerializingStrategy<? extends T, Node, Node, IOException, Node, Node> strategy = serializingStrategySupport.findStrategy( subType );
      Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
      return strategy.deserialize( deserializeFrom, resolvedVersion );

    } catch ( NotFoundException e ) {
      throw new IOException( "Invalid node. Expected property <" + PROPERTY_SUB_TYPE + "> but only contained <" + deserializeFrom.getPropertyKeys() + "> @ " + deserializeFrom.getId(), e );
    }
  }

  @Nonnull
  public Collection<? extends SerializingStrategy<? extends T, Node, Node, IOException, Node, Node>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }

  @Nonnull
  public VersionMapping addStrategy( @Nonnull SerializingStrategy<? extends T, Node, Node, IOException, Node, Node> strategy ) {
    return serializingStrategySupport.addStrategy( strategy );
  }

  @Nonnull
  public SerializingStrategySupport<T, Node, Node, IOException, Node, Node> getSerializingStrategySupport() {
    return serializingStrategySupport;
  }
}
