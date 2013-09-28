package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.neo4j.AbstractNeo4JTest;
import org.junit.*;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BasicNeo4JTest extends AbstractNeo4JTest {
  @Test
  public void testReferenceNode() throws Exception {
    try ( Transaction tx = graphDb.beginTx() ) {
      Node referenceNode = graphDb.getNodeById( 0 );
      assertThat( referenceNode.getRelationships() ).hasSize( 0 );
      assertThat( referenceNode ).isNotNull();
    }
  }

  @Test
  public void testById() throws Exception {
    Node node;
    try ( Transaction tx = graphDb.beginTx() ) {
      node = graphDb.createNode();
      node.setProperty( "name", "Nancy" );
      tx.success();
    }

    try ( Transaction tx = graphDb.beginTx() ) {
      assertThat( graphDb.getNodeById( 1 ) ).isEqualTo( node );
    }
  }

  @Test
  public void testIt() throws Exception {
    Node node;

    try ( Transaction tx = graphDb.beginTx() ) {
      node = graphDb.createNode();
      node.setProperty( "name", "Nancy" );
      tx.success();
    }

    assertThat( node ).isNotNull();
    assertThat( node.getId() ).isEqualTo( 1 );

    assert node != null;

    // The node should have an id greater than 0, which is the id of the
    // reference node.

    try ( Transaction tx = graphDb.beginTx() ) {
      assertThat( node.getId() ).isGreaterThan( 0 );

      // Retrieve a node by using the id of the created node. The id's and
      // property should match.
      Node foundNode = graphDb.getNodeById( node.getId() );

      assertThat( foundNode.getId() ).isEqualTo( node.getId() );
      assertThat( foundNode.getProperty( "name" ) ).isEqualTo( "Nancy" );

      tx.success();
    }
  }
}
