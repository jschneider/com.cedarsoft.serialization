package com.cedarsoft.serialization.neo4j.test.utils;

import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule that provides neo4j databases
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
        GraphDatabaseService db = createDb();
        try {
          base.evaluate();
        } catch ( Throwable e ) {
          dump(db);
          throw e;
        } finally {
          after();
        }
      }
    }, description );
  }

  public static void dump( @Nonnull GraphDatabaseService db ) {
    if ( "true".equalsIgnoreCase( System.getProperty( "neo4j.dumpOnError" ) ) ) {
      try {
        System.err.println( dumpToText(db) );
        Graphviz.toPng( db );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  @Nonnull
  public static String dumpToText( @Nonnull GraphDatabaseService db ) {
    try ( Transaction tx = db.beginTx() ) {
      Result result = db.execute("MATCH (n)\n" + "RETURN n;");
      return result.resultAsString();
    }
  }

  @Nonnull
  private final List<GraphDatabaseService> dbs = new ArrayList<>();

  @Nonnull
  public GraphDatabaseService createDb() throws IOException {
    GraphDatabaseService db = new TestGraphDatabaseFactory().newImpermanentDatabase(tmp.newFolder());
    dbs.add( db );
    return db;
  }

  private void after() {
    for ( GraphDatabaseService db : dbs ) {
      db.shutdown();
    }
  }
}
