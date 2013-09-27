package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.test.neo4j.AbstractNeo4JTest;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.ecyrd.speed4j.StopWatch;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.junit.*;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class AbstractNeo4jSerializerTest extends AbstractNeo4JTest {
  @Test
  public void testIt() throws Exception {
    Person person = new Person( "Martha Musterfrau", new Address( "Musterstraße 7", "Musterstadt" ), ImmutableList.of( new Email( "1" ), new Email( "2" ), new Email( "3" ) ) );

    Node node;
    try ( Transaction tx = graphDb.beginTx() ) {
      //First Serialize
      node = serialize( person );
      tx.success();
    }

    //Now deserialize
    try ( Transaction tx = graphDb.beginTx() ) {
      Person deserialized = deserialize( node );
      assertThat( deserialized.getName() ).isEqualTo( person.getName() );
      assertThat( deserialized.getAddress().getStreet() ).isEqualTo( person.getAddress().getStreet() );
      assertThat( deserialized.getMails() ).hasSize( 3 );
      assertThat( deserialized.getMails() ).containsExactly( new Email( "1" ), new Email( "2" ), new Email( "3" ) );
      tx.success();
    }
  }


  @Test
  public void testPerformanceSerialization() throws Exception {
    int transactionCount = 10000;
    Person person = new Person( "Martha Musterfrau", new Address( "Musterstraße 7", "Musterstadt" ), ImmutableList.of( new Email( "1" ), new Email( "2" ), new Email( "3" ) ) );

    StopWatch stopWatch = new StopWatch( getClass().getName() );

    try ( Transaction tx = graphDb.beginTx() ) {

      for ( int i = 0; i < transactionCount; i++ ) {
        //First Serialize
        Node node = serialize( person );
      }
      tx.success();
    }

    stopWatch.stop( "created " + transactionCount + " nodes" );
    System.out.println( stopWatch.toString( transactionCount ) );
  }

  private Person deserialize( Node node ) throws IOException {
    PersonSerializer personSerializer = new PersonSerializer();
    return personSerializer.deserialize( node );
  }

  @Nonnull
  private final PersonSerializer personSerializer = new PersonSerializer();

  private Node serialize( Person person ) throws IOException {
    Node node;
    try ( Transaction tx = graphDb.beginTx() ) {
      node = graphDb.createNode();
      personSerializer.serialize( person, node );
      tx.success();
    }
    return node;
  }

  public static class Person {
    @Nonnull
    private final String name;
    @Nonnull
    private final Address address;
    @Nonnull
    private final List<? extends Email> mails;

    public Person( @Nonnull String name, @Nonnull Address address, @Nonnull List<? extends Email> mails ) {
      this.name = name;
      this.address = address;
      this.mails = mails;
    }

    @Nonnull
    public List<? extends Email> getMails() {
      return mails;
    }

    @Nonnull
    public Address getAddress() {
      return address;
    }

    @Nonnull
    public String getName() {
      return name;
    }
  }

  public static class Email {
    @Nonnull
    private final String mail;

    public Email( @Nonnull String mail ) {
      this.mail = mail;
    }

    @Nonnull
    public String getMail() {
      return mail;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null || getClass() != obj.getClass() ) {
        return false;
      }

      Email that = ( Email ) obj;

      return Objects.equal( this.mail, that.mail );
    }

    @Override
    public int hashCode() {
      return Objects.hashCode( mail );
    }

    @Override
    public String toString() {
      return Objects.toStringHelper( this )
        .addValue( mail )
        .toString();
    }
  }

  public static class Address {
    @Nonnull
    private final String street;
    @Nonnull
    private final String town;

    public Address( @Nonnull String street, @Nonnull String town ) {
      this.street = street;
      this.town = town;
    }

    @Nonnull
    public String getStreet() {
      return street;
    }

    @Nonnull
    public String getTown() {
      return town;
    }
  }


  public static class PersonSerializer extends AbstractNeo4jSerializer<Person> {
    public PersonSerializer() {
      super( "com.cedarsoft.test.person", VersionRange.single( 1, 0, 0 ) );

      getDelegatesMappings().add( new AddressSerializer() ).responsibleFor( Address.class )
        .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
      getDelegatesMappings().add( new EmailSerializer() ).responsibleFor( Email.class )
        .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );


      getDelegatesMappings().verify();
    }

    @Override
    public void serialize( @Nonnull Node serializeTo, @Nonnull Person object, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      serializeTo.setProperty( "name", object.getName() );

      serializeWithRelation( object.getAddress(), Address.class, serializeTo, Relations.ADDRESS, formatVersion );
      for ( Email email : object.getMails() ) {
        serializeWithRelation( email, Email.class, serializeTo, Relations.EMAIL, formatVersion );
      }
    }

    @Nonnull
    @Override
    public Person deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      String name = ( String ) deserializeFrom.getProperty( "name" );

      Address address = deserializeWithRelationship( Address.class, Relations.ADDRESS, deserializeFrom, formatVersion );

      List<? extends Email> emails = deserializeWithRelationships( Email.class, Relations.EMAIL, deserializeFrom, formatVersion );
      return new Person( name, address, emails );
    }

    public enum Relations implements RelationshipType {
      ADDRESS,
      EMAIL
    }
  }


  public static class EmailSerializer extends AbstractNeo4jSerializer<Email> {
    public EmailSerializer() {
      super( "com.cedarsoft.test.email", VersionRange.single( 1, 0, 0 ) );
    }

    @Override
    public void serialize( @Nonnull Node serializeTo, @Nonnull Email object, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      serializeTo.setProperty( "mail", object.getMail() );
    }

    @Nonnull
    @Override
    public Email deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      String mail = ( String ) deserializeFrom.getProperty( "mail" );
      return new Email( mail );
    }
  }

  public static class AddressSerializer extends AbstractNeo4jSerializer<Address> {
    public AddressSerializer() {
      super( "com.cedarsoft.test.address", VersionRange.single( 1, 0, 0 ) );
    }

    @Override
    public void serialize( @Nonnull Node serializeTo, @Nonnull Address object, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      serializeTo.setProperty( "street", object.getStreet() );
      serializeTo.setProperty( "town", object.getTown() );
    }

    @Nonnull
    @Override
    public Address deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      String street = ( String ) deserializeFrom.getProperty( "street" );
      String town = ( String ) deserializeFrom.getProperty( "town" );
      return new Address( street, town );
    }
  }
}
