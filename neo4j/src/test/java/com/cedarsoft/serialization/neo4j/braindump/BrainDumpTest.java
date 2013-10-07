package com.cedarsoft.serialization.neo4j.braindump;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.junit.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;

import com.cedarsoft.serialization.neo4j.AbstractNeo4JTest;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BrainDumpTest extends AbstractNeo4JTest {
  @Nonnull
  public static final String PROPERTY_URI = "uri";

  @Test
  public void testBasic() throws Exception {
    //create the index
    IndexDefinition index;
    try (Transaction tx = graphDb.beginTx()) {
      IndexDefinition indexDefinition = graphDb.schema().indexFor(Types.INFORMATION).on(PROPERTY_URI).create();
      tx.success();
      index = indexDefinition;
    }

    try (Transaction tx = graphDb.beginTx()) {
      graphDb.schema().awaitIndexOnline(index, 10, TimeUnit.SECONDS);
      tx.success();
    }


    //Now create a single
    try (Transaction tx = graphDb.beginTx()) {
      {
        Node node = graphDb.createNode(Types.INFORMATION);
        node.setProperty(PROPERTY_URI, "http://www.google.de");

        Node identity = graphDb.createNode(Types.IDENTITY);
        identity.setProperty("username", "mustermann");
        node.createRelationshipTo(identity, Relations.CONTAINS);

        Node authentication = graphDb.createNode(Types.PASSWORD);
        authentication.setProperty("password", "geheim");
        identity.createRelationshipTo(authentication, Relations.AUTHENTICATION);

        Node personalData = graphDb.createNode(Types.MY_DATA);
        personalData.setProperty("name", "Markus Mustermann");
        personalData.setProperty("email", "markus@mustermann.de");
        identity.createRelationshipTo(personalData, Relations.CONTAINS);
      }

      {
        Node node = graphDb.createNode(Types.INFORMATION);
        node.setProperty(PROPERTY_URI, "http://www.github.com");

        Node identity = graphDb.createNode(Types.IDENTITY);
        identity.setProperty("username", "mustermann");
        node.createRelationshipTo(identity, Relations.CONTAINS);

        {
          Node authentication = graphDb.createNode(Types.PASSWORD);
          authentication.setProperty("password", "geheim");
          identity.createRelationshipTo(authentication, Relations.AUTHENTICATION);
        }
        {
          Node authentication = graphDb.createNode(Types.RSA);
          authentication.setProperty("public-key", "#FE324B324");
          authentication.setProperty("private-key", "#23B4242362AB");
          identity.createRelationshipTo(authentication, Relations.AUTHENTICATION);
        }

        Node personalData = graphDb.createNode(Types.MY_DATA);
        personalData.setProperty("name", "Markus Mustermann");
        personalData.setProperty("email", "markus@mustermann.de");
        identity.createRelationshipTo(personalData, Relations.CONTAINS);
      }

      tx.success();
    }


    try (Transaction tx = graphDb.beginTx()) {
      assertThat(graphDb.findNodesByLabelAndProperty(Types.INFORMATION, PROPERTY_URI, "http://www.google.de")).hasSize(1);
      assertThat(graphDb.findNodesByLabelAndProperty(Types.INFORMATION, PROPERTY_URI, "http://www.github.de")).hasSize(1);
      tx.success();
    }


  }

  public enum Types implements Label {
    INFORMATION,
    PASSWORD,
    RSA,
    MY_DATA,
    IDENTITY,
  }

  public enum Relations implements RelationshipType {
    AUTHENTICATION,
    CONTAINS

  }

}
