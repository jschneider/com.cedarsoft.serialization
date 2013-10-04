package com.cedarsoft.serialization.neo4j;

import org.junit.*;
import org.junit.rules.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class AbstractNeo4JTest {
  protected GraphDatabaseService graphDb;

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Before
  public void prepareTestDatabase() throws IOException {
    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase( tmp.newFolder().getAbsolutePath() );
  }

  @After
  public void destroyTestDatabase() {
    graphDb.shutdown();
  }
}
