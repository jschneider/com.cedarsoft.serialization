package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.neo4j.sample.PersonSerializer;
import org.junit.*;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class CyperTest extends AbstractNeo4JTest {


  @Test
  public void testCyper1() throws Exception {
    ExecutionEngine engine = new ExecutionEngine( graphDb );


    try ( Transaction tx = graphDb.beginTx() ) {
      Node node1 = graphDb.createNode();
      node1.setProperty( "name", "MM" );

      Node node2 = graphDb.createNode();
      Node node3 = graphDb.createNode();
      Node node4 = graphDb.createNode();

      node1.createRelationshipTo( node2, PersonSerializer.Relations.ADDRESS );
      node1.createRelationshipTo( node3, PersonSerializer.Relations.ADDRESS );
      node2.createRelationshipTo( node3, PersonSerializer.Relations.ADDRESS );
      node3.createRelationshipTo( node4, PersonSerializer.Relations.ADDRESS );

      tx.success();
    }

    String query = "start n=node(*) where n.name = 'MM' return n, n.name";
    assertThat( engine.execute( query ).dumpToString().trim() ).isEqualTo( "+-----------------------------+\n" +
                                                            "| n                  | n.name |\n" +
                                                            "+-----------------------------+\n" +
                                                            "| Node[0]{name:\"MM\"} | \"MM\"   |\n" +
                                                            "+-----------------------------+\n" +
                                                            "1 row" );

    assertThat( engine.execute( query ).columns() ).containsExactly( "n", "n.name" );

    boolean called = false;
    for ( Node node : IteratorUtil.asIterable( engine.execute( query ).<Node>columnAs( "n" ) ) ) {
      assertThat( node.getProperty( "name" ) ).isEqualTo( "MM" );
      called = true;
    }
    assertThat( called ).isTrue();
  }
}
