package com.cedarsoft.serialization.neo4j.test;

import com.ecyrd.speed4j.StopWatch;
import org.junit.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class PerformanceTest {
  private GraphDatabaseService graphDb;

  @Before
  public void prepareTestDatabase() {
    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
  }

  @After
  public void destroyTestDatabase() {
    graphDb.shutdown();
  }

  @Ignore
  @Test
  public void testName() throws Exception {
    int nodes = 50000;
    int nodesPerTransaction = 30;
    int transactionCount = nodes / nodesPerTransaction;

    //Warm up
    for ( int i = 0; i < 5; i++ ) {
      run( transactionCount, nodesPerTransaction );
    }

    //Now live
    for ( int i = 0; i < 5; i++ ) {
      StopWatch stopWatch = new StopWatch( getClass().getName() );

      run( transactionCount, nodesPerTransaction );
      stopWatch.stop( "created " + transactionCount * nodesPerTransaction + " nodes" );
      System.out.println( stopWatch.toString( transactionCount * nodesPerTransaction ) );
    }
  }

  public void run( int transactionCount, int nodesPerTransaction ) throws Exception {
    Node root;
    try ( Transaction tx = graphDb.beginTx() ) {
      root = graphDb.createNode();
      tx.success();
    }

    for ( int i = 0; i < transactionCount; i++ ) {
      try ( Transaction tx = graphDb.beginTx() ) {
        for ( int j = 0; j < nodesPerTransaction; j++ ) {
          Node node = graphDb.createNode();
          node.setProperty( "name", 17);
          node.setProperty( "name2", 12.4 );
          root.createRelationshipTo( node, Relations.MARRIED );
        }
        tx.success();
      }
    }
  }
}
