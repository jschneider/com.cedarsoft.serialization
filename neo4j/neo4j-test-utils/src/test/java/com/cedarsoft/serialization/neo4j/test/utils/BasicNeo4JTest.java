package com.cedarsoft.serialization.neo4j.test.utils;

import org.junit.*;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.Index;
import org.neo4j.jmx.JmxUtils;

import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BasicNeo4JTest extends AbstractNeo4JTest {
  @Test
  public void testJmx() throws Exception {
    ObjectName objectName = JmxUtils.getObjectName( graphDb, "Kernel" );
    String version = JmxUtils.getAttribute( objectName, "KernelVersion" );

    assertThat( version ).contains( "neo4j" ).contains( "version: 3.1.0" );
  }

  @Test
  public void testLegacyIndex() throws Exception {
    graphDb.registerTransactionEventHandler( new TransactionEventHandler<Object>() {
      @Override
      public Object beforeCommit( TransactionData data ) throws Exception {
        System.out.println( "com.cedarsoft.serialization.neo4j.test.utils.BasicNeo4JTest.beforeCommit" );
        System.out.println( data );
        System.out.println( "Created Nodes: " + data.createdNodes() );
        System.out.println( "Created Relationships: " + data.createdRelationships() );
        return null;
      }

      @Override
      public void afterCommit( TransactionData data, Object state ) {
        System.out.println( "com.cedarsoft.serialization.neo4j.test.utils.BasicNeo4JTest.afterCommit" );
        System.out.println( data );
      }

      @Override
      public void afterRollback( TransactionData data, Object state ) {
        System.out.println( "com.cedarsoft.serialization.neo4j.test.utils.BasicNeo4JTest.afterRollback" );
        System.out.println( data );
      }
    } );

    try ( Transaction tx = graphDb.beginTx() ) {
      Index<Node> index = graphDb.index().forNodes( "myIndex" );

      Node node = graphDb.createNode();
      assertThat( node.getRelationships() ).hasSize( 0 );
      assertThat( node ).isNotNull();

      index.add( node, "myKey", 1 );

      Relationship relationship = node.createRelationshipTo( node, TestRelations.MARRIED );
      graphDb.index().forRelationships( "rels" ).add( relationship, "myKey", 7 );

      tx.success();
    }

    try ( Transaction tx = graphDb.beginTx() ) {
      Index<Node> index = graphDb.index().forNodes( "myIndex" );

      assertThat( index.getEntityType() ).isEqualTo( Node.class );
      assertThat( index.getName() ).isEqualTo( "myIndex" );

      assertThat( ( Iterable<?> ) index.get( "myKey", 1 ) ).hasSize( 1 );
      assertThat( index.get( "myKey", 1 ).getSingle() ).isNotNull();
      assertThat( ( Iterable<?> ) index.query( "myKey", 1 ) ).hasSize( 1 );
      assertThat( ( Iterable<?> ) index.get( "myKey", 0 ) ).hasSize( 0 );
      assertThat( ( Iterable<?> ) index.query( "myKey", 0 ) ).hasSize( 0 );

      assertThat( ( Iterable<?> ) index.query( "myKey:\"1\"" ) ).hasSize( 1 );
      assertThat( ( Iterable<?> ) index.query( "myKey:\"0\"" ) ).hasSize( 0 );
    }
  }

  @Test
  public void testIndexDuplicates() throws Exception {
    try ( Transaction tx = graphDb.beginTx() ) {
      Index<Node> index = graphDb.index().forNodes( "myIndex" );

      index.add( graphDb.createNode(), "myKey", 1 );
      index.add( graphDb.createNode(), "myKey", 1 );

      tx.success();
    }

    try ( Transaction tx = graphDb.beginTx() ) {
      Index<Node> index = graphDb.index().forNodes( "myIndex" );
      assertThat( index.getEntityType() ).isEqualTo( Node.class );
      assertThat( index.getName() ).isEqualTo( "myIndex" );

      assertThat( ( Iterable<?> ) index.query( "myKey", 1 ) ).hasSize( 2 );
      assertThat( ( Iterable<?> ) index.query( "myKey", 0 ) ).hasSize( 0 );
    }
  }

  @Test
  public void testSelfRef() throws Exception {
    try ( Transaction tx = graphDb.beginTx() ) {
      Node node = graphDb.createNode();
      assertThat( node.getRelationships() ).hasSize( 0 );
      assertThat( node ).isNotNull();

      Relationship relationship = node.createRelationshipTo( node, TestRelations.SON );
      assertThat( relationship.getStartNode() ).isEqualTo( relationship.getEndNode() );

      assertThat( node.getRelationships() ).hasSize( 1 );

      tx.success();
    }
  }

  @Test
  public void testById() throws Exception {
    Node node;
    try ( Transaction tx = graphDb.beginTx() ) {
      node = graphDb.createNode();
      node.setProperty( "name", "Nancy" );
      tx.success();

      assertThat( node.getId() ).isEqualTo( 0 );
    }

    try ( Transaction tx = graphDb.beginTx() ) {
      assertThat( graphDb.getNodeById( 0 ) ).isEqualTo( node );
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
    assertThat( node.getId() ).isEqualTo( 0 );

    assert node != null;

    // The node should have an id greater than 0, which is the id of the
    // reference node.

    try ( Transaction tx = graphDb.beginTx() ) {
      assertThat( node.getId() ).isEqualTo( 0 );

      // Retrieve a node by using the id of the created node. The id's and
      // property should match.
      Node foundNode = graphDb.getNodeById( node.getId() );

      assertThat( foundNode.getId() ).isEqualTo( node.getId() );
      assertThat( foundNode.getProperty( "name" ) ).isEqualTo( "Nancy" );

      tx.success();
    }
  }
}
