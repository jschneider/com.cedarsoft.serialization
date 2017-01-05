package com.cedarsoft.serialization.neo4j.test.utils;

import org.junit.*;
import org.neo4j.graphdb.GraphDatabaseService;

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
    graphDb = neo4jRule.createDb();
  }
}
