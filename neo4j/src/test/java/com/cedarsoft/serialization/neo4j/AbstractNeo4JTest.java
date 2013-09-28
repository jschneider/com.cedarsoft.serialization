package com.cedarsoft.serialization.neo4j;

import org.junit.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class AbstractNeo4JTest {
  protected GraphDatabaseService graphDb;

  @Before
  public void prepareTestDatabase() {
    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
  }

  @After
  public void destroyTestDatabase() {
    graphDb.shutdown();
  }
}
