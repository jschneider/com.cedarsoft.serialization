package com.cedarsoft.serialization.neo4j;

import org.junit.*;
import org.junit.rules.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class AbstractNeo4JTest {
  @Rule
  public Neo4jRule neo4jRule = new Neo4jRule(  );

  protected GraphDatabaseService graphDb;

  @Before
  public void prepareTestDatabase() throws IOException {
    graphDb = neo4jRule.getGraphDb();
  }
}
