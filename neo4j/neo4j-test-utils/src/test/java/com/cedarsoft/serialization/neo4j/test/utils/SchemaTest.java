package com.cedarsoft.serialization.neo4j.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SchemaTest extends AbstractNeo4JTest {
  @Test
  public void testSchemaTest() throws Exception {
    IndexDefinition indexDefinition;

    try (Transaction tx = graphDb.beginTx()) {
      Schema schema = graphDb.schema();
      indexDefinition = schema.indexFor(NodeLabel.USER).on("username").create();
      tx.success();
    }


    try (Transaction tx = graphDb.beginTx()) {
      Schema schema = graphDb.schema();
      schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
    }

    try (Transaction tx = graphDb.beginTx()) {

      // Create some users
      for (int id = 0; id < 100; id++) {
        Node userNode = graphDb.createNode(NodeLabel.USER);
        userNode.setProperty("username", "user" + id + "@neo4j.org");
      }
      System.out.println("Users created");
      tx.success();
    }


    int idToFind = 45;
    String nameToFind = "user" + idToFind + "@neo4j.org";

    try (Transaction tx = graphDb.beginTx()) {
      ResourceIterator<Node> users = graphDb.findNodes(NodeLabel.USER, "username", nameToFind);
      List<Node> userNodes = new ArrayList<>();
      while (users.hasNext()) {
        userNodes.add(users.next());
      }

      for (Node node : userNodes) {
        System.out.println("The username of user " + idToFind + " is " + node.getProperty("username"));
      }
    }
  }

  public enum NodeLabel implements Label {
    USER
  }
}
