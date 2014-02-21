package com.cedarsoft.serialization.neo4j.sample;

import com.cedarsoft.serialization.neo4j.AbstractNeo4JTest;
import com.ecyrd.speed4j.StopWatch;
import com.google.common.collect.ImmutableList;
import org.junit.*;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SampleNeo4jSerializerTest extends AbstractNeo4JTest {
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
  public void testWrongLabel() throws Exception {
    Person person = new Person( "Martha Musterfrau", new Address( "Musterstraße 7", "Musterstadt" ), ImmutableList.of( new Email( "1" ), new Email( "2" ), new Email( "3" ) ) );

    Node node;
    try ( Transaction tx = graphDb.beginTx() ) {
      node = serialize( person );

      //remove label
      node.removeLabel(personSerializer.getTypeLabel());
      node.addLabel(DynamicLabel.label("lab1"));
      node.addLabel(DynamicLabel.label("lab2"));
      node.addLabel(DynamicLabel.label("lab3"));

      tx.success();
    }

    //Now deserialize
    try {
      try ( Transaction tx = graphDb.beginTx() ) {
        deserialize(node);
      }
      fail("Where is the Exception");
    } catch (IOException e) {
      assertThat(e.getCause()).isNotNull().hasMessage("Invalid type. Expected <com.cedarsoft.test.person> but found <[lab1, lab2, lab3]>");
    }
  }

  @Ignore
  @Test
  public void testPerformanceSerialization() throws Exception {
    int transactionCount = 10000;
    Person person = new Person( "Martha Musterfrau", new Address( "Musterstraße 7", "Musterstadt" ), ImmutableList.of( new Email( "1" ), new Email( "2" ), new Email( "3" ) ) );

    StopWatch stopWatch = new StopWatch( getClass().getName() );

    try ( Transaction tx = graphDb.beginTx() ) {

      for ( int i = 0; i < transactionCount; i++ ) {
        //First Serialize
        Node node = serialize( person );
        assertThat( node ).isNotNull();
      }
      tx.success();
    }

    stopWatch.stop( "created " + transactionCount + " nodes" );
    System.out.println( stopWatch.toString( transactionCount ) );
  }

  @Nonnull
  private  Person deserialize( @Nonnull Node node ) throws IOException {
    return personSerializer.deserialize( node );
  }

  @Nonnull
  private final PersonSerializer personSerializer = new PersonSerializer();

  @Nonnull
  private Node serialize( @Nonnull Person person ) throws IOException {
    Node node;
    try ( Transaction tx = graphDb.beginTx() ) {
      node = graphDb.createNode();
      personSerializer.serialize( person, node );
      tx.success();
    }
    return node;
  }


}
