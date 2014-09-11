package com.cedarsoft.serialization.neo4j.test.utils;

import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * The type Person serializer.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class PersonSerializer extends AbstractNeo4jSerializer<Person> {
  /**
   * Instantiates a new Person serializer.
   */
  public PersonSerializer() {
    super( "com.cedarsoft.test.person", VersionRange.single( 1, 0, 0 ) );

    getDelegatesMappings().add( new AddressSerializer() ).responsibleFor( Address.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( new EmailSerializer() ).responsibleFor( Email.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );


    getDelegatesMappings().verify();
  }

  @Override
  protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Person object, @Nonnull Version formatVersion ) throws IOException {
    serializeTo.setProperty( "name", object.getName() );

    serializeWithRelationship( object.getAddress(), Address.class, serializeTo, Relations.ADDRESS, formatVersion );
    serializeWithRelationships( object.getMails(),Email.class, serializeTo, Relations.EMAIL, formatVersion );
  }

  @Nonnull
  @Override
  public Person deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
    verifyVersionReadable( formatVersion );

    String name = ( String ) deserializeFrom.getProperty( "name" );

    Address address = deserializeWithRelationship( Address.class, Relations.ADDRESS, deserializeFrom, formatVersion );

    List<? extends Email> emails = deserializeWithRelationships( Email.class, Relations.EMAIL, deserializeFrom, formatVersion );
    return new Person( name, address, emails );
  }

  /**
   * The enum Relations.
   */
  public enum Relations implements RelationshipType {
    /**
     * The ADDRESS.
     */
    ADDRESS,
    /**
     * The EMAIL.
     */
    EMAIL
  }
}
