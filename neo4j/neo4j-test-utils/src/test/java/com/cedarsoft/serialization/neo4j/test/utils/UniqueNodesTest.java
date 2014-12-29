package com.cedarsoft.serialization.neo4j.test.utils;

import org.junit.*;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.UniqueFactory;

import javax.annotation.Nonnull;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UniqueNodesTest extends AbstractNeo4JTest {

  public static final String PROPERTY_LOGIN = "login";

  @Test
  public void testUnique1() throws Exception {
    //org.neo4j.graphdb.GraphDatabaseService
    UniqueFactory<Node> factory = createFactory();

    try ( Transaction tx = graphDb.beginTx() ) {
      Node musterfrau = factory.getOrCreate( PROPERTY_LOGIN, "musterfrau" );
      musterfrau.setProperty( "asdf", 1 );

      Node musterfrau2 = factory.getOrCreate( PROPERTY_LOGIN, "musterfrau" );

      assertThat( musterfrau ).isEqualTo( musterfrau2 );
      assertThat( musterfrau2.getProperty( "asdf" ) ).isEqualTo( 1 );
      tx.success();
    }
  }

  @Nonnull
  private UniqueFactory<Node> createFactory() {
    try ( Transaction tx = graphDb.beginTx() ) {
      UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory( graphDb, "users" ) {
        @Override
        protected void initialize( Node created, Map<String, Object> properties ) {
          created.setProperty( PROPERTY_LOGIN, properties.get( PROPERTY_LOGIN ) );
        }
      };
      tx.success();
      return factory;
    }
  }
}
