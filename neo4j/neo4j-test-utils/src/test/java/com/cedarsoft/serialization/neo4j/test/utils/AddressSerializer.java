package com.cedarsoft.serialization.neo4j.test.utils;

import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
* @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
*/
public class AddressSerializer extends AbstractNeo4jSerializer<Address> {
  /**
   * Instantiates a new Address serializer.
   */
  public AddressSerializer() {
    super( "com.cedarsoft.test.address", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Address object, @Nonnull Version formatVersion ) {
    serializeTo.setProperty( "street", object.getStreet() );
    serializeTo.setProperty( "town", object.getTown() );
  }

  @Nonnull
  @Override
  public Address deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
    verifyVersionReadable( formatVersion );

    String street = ( String ) deserializeFrom.getProperty( "street" );
    String town = ( String ) deserializeFrom.getProperty( "town" );
    return new Address( street, town );
  }
}
