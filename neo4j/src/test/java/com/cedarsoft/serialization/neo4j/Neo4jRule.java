package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.neo4j.utils.Graphviz;
import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Neo4jRule implements TestRule {
  @Nonnull
  private final TemporaryFolder tmp = new TemporaryFolder();

  @Override
  public Statement apply( final Statement base, Description description ) {
    return tmp.apply( new Statement() {
      @Override
      public void evaluate() throws Throwable {
        before();
        try {
          base.evaluate();
        } catch ( Throwable e ) {
          dump();
          throw e;
        } finally {
          after();
        }
      }
    }, description );
  }

  public void dump() {
    if ( "true".equalsIgnoreCase( System.getProperty( "neo4j.dumpOnError" ) ) ) {
      try {
        Graphviz.toPng( getGraphDb() );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  private GraphDatabaseService graphDb;

  private void before() throws IOException {
    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase( tmp.newFolder().getAbsolutePath() );
  }

  private void after() {
    graphDb.shutdown();
  }

  @Nonnull
  public GraphDatabaseService getGraphDb() {
    if ( graphDb == null ) {
      throw new IllegalStateException( "graph db is null" );
    }
    return graphDb;
  }
}
