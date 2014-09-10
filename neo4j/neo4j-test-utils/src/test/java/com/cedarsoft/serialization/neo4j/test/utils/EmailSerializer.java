package com.cedarsoft.serialization.neo4j.test.utils;

import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * The type Email serializer.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class EmailSerializer extends AbstractNeo4jSerializer<Email> {
  /**
   * Instantiates a new Email serializer.
   */
  public EmailSerializer() {
    super( "com.cedarsoft.test.email", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Email object, @Nonnull Version formatVersion ) {
    serializeTo.setProperty( "mail", object.getMail() );
  }

  @Nonnull
  @Override
  public Email deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
    verifyVersionReadable( formatVersion );

    String mail = ( String ) deserializeFrom.getProperty( "mail" );
    return new Email( mail );
  }
}
