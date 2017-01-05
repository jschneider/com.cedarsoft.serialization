package com.cedarsoft.serialization.neo4j.test.utils;

import com.google.common.io.ByteStreams;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Visitor;
import org.neo4j.walk.Walker;

import javax.annotation.Nonnull;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Graphviz {
  private Graphviz() {
  }

  public static void toPng( @Nonnull final GraphDatabaseService graphDb ) throws IOException, InterruptedException {
    File targetFile = File.createTempFile( "graphviz", ".png" );
    ProcessBuilder processBuilder = new ProcessBuilder( "dot", "-Tpng", "-o", targetFile.getAbsolutePath() );
    Process process = processBuilder.start();

    //Now output the dot file
    Walker walker = new Walker() {
      @Override
      public <R, E extends Throwable> R accept( Visitor<R, E> visitor ) throws E {
        for (Node node : graphDb.getAllNodes()) {
          visitor.visitNode( node );
          for ( Relationship edge : node.getRelationships( Direction.OUTGOING ) ) {
            visitor.visitRelationship( edge );
          }
        }
        return visitor.done();
      }
    };

    GraphvizWriter writer = new GraphvizWriter();
    try ( Transaction tx = graphDb.beginTx() ) {
      writer.emit( process.getOutputStream(), walker );
      tx.success();
    }

    process.getOutputStream().close();

    int result = process.waitFor();
    if ( result != 0 ) {
      byte[] errorOut = ByteStreams.toByteArray( process.getErrorStream() );
      throw new IllegalStateException( "Did not work: " + new String( errorOut, StandardCharsets.UTF_8 ) );
    }

    Desktop.getDesktop().open( targetFile );
  }
}
