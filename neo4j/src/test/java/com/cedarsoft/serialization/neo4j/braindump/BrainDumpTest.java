package com.cedarsoft.serialization.neo4j.braindump;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import com.cedarsoft.serialization.neo4j.AbstractNeo4JTest;
import com.google.common.base.Joiner;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BrainDumpTest extends AbstractNeo4JTest {
  @Nonnull
  public static final String PROPERTY_URI = "uri";
  public static final String PROPERTY_TITLE = "title";
  public static final String PROPERTY_CONTENT = "content";
  public static final String PROPERTY_VALUE = "value";
  public static final String PROPERTY_KEY = "key";

  @Before
  public void prepareIndex() {
    //create the index
    List<IndexDefinition> indexes = new ArrayList<>();

    //Create the indexes
    try (Transaction tx = graphDb.beginTx()) {
      indexes.add(graphDb.schema().indexFor(Types.INFORMATION).on(PROPERTY_URI).create());

      indexes.add(graphDb.schema().indexFor(Types.NOTE).on(PROPERTY_TITLE).create());
      indexes.add(graphDb.schema().indexFor(Types.NOTE).on(PROPERTY_CONTENT).create());

      indexes.add(graphDb.schema().indexFor(Types.TOPIC).on(PROPERTY_VALUE).create());

      tx.success();
    }

    //Wait until the indexes are online
    try (Transaction tx = graphDb.beginTx()) {
      for (IndexDefinition index : indexes) {
        graphDb.schema().awaitIndexOnline(index, 10, TimeUnit.SECONDS);
      }
      tx.success();
    }
  }

  @Test
  public void testSecond() throws Exception {
    try (Transaction tx = graphDb.beginTx()) {
      //Prepare topics
      Node linux = createTopic("Linux");
      Node ubuntu = createTopic("Ubuntu");
      Node upgrade = createTopic("Upgrade", ubuntu);
      Node node1310 = createTopic("13.10", ubuntu);
      Node apache = createTopic("Apache", linux);
      relates(apache, ubuntu);

      //This is my current topic I am working under
      Node upgradeTo1310 = createTopic("Upgrade to 13.10", ubuntu, upgrade, node1310);

      Node hint = createNote("VHosts-Problem bei Apache-Upgrade auf 2.4", "Beim Upgrade auf 2.4 muss man beachten, dass die VHosts-Konfigurations-Files auf \".conf\" enden müssen.");
      toTopics(hint, upgradeTo1310); //Automatically added because this is the curren topic
      toTopics(hint, apache); //manually added, because it makes sense - maybe some kind of auto hint added?

      toTopics(createInformation("Latest LTS", "12.04.1"), ubuntu);
      toTopics(createInformation("Code Name", "Saucy Salamander"), node1310);

      toTopics(createInformation(PROPERTY_URI, "https://ubuntu.com"), ubuntu);

      tx.success();
    }


    try (Transaction tx = graphDb.beginTx()) {
      //Find a topic
      printTopic(findTopic("Upgrade to 13.10"));
      printTopic(findTopic("Ubuntu"));

      printTopicAll(findTopic("Ubuntu"));

      tx.success();
    }


    //Graphviz.toPng(graphDb);@
  }

  private void printTopicAll(@Nonnull Node topic) {
    System.out.println("*All* entries stored under <" + topic.getProperty(PROPERTY_VALUE) + ">: ");
    for (Path path : getAssignedNonTopicNodesRecursive(topic)) {
      Node node = path.endNode();
      System.out.println("\t- " + getLabels(node) + ": " + toString(node));
    }
  }

  @Nonnull
  private static Iterable<Path> getSubTopics(@Nonnull Node topic) {
    Traverser traverse = Traversal.description()
      .relationships(Relations.TO_TOPIC, Direction.INCOMING)
      .evaluator(Evaluators.toDepth(1)).traverse(topic);
    return traverse;
  }

  @Nonnull
  private static Iterable<Path> getSubEntries(@Nonnull Node topic) {
    Traverser traverse = Traversal.description()
      .relationships(Relations.TO_PARENT, Direction.INCOMING)
      .evaluator(Evaluators.toDepth(1)).traverse(topic);
    return traverse;
  }

  @Nonnull
  private static Iterable<Path> getAssignedNonTopicNodesRecursive(@Nonnull Node topic) {
    return Traversal.description()
      .relationships(Relations.TO_TOPIC, Direction.INCOMING)
      .relationships(Relations.TO_PARENT, Direction.INCOMING)
      .evaluator(new Evaluator() {
        @Override
        public Evaluation evaluate(Path path) {
          @Nullable Relationship relationship = path.lastRelationship();
          if (relationship == null) {
            return Evaluation.EXCLUDE_AND_CONTINUE;
          }

          if (relationship.isType(Relations.TO_TOPIC)) {
            return Evaluation.INCLUDE_AND_CONTINUE;
          }
          if (relationship.isType(Relations.TO_PARENT)) {
            return Evaluation.EXCLUDE_AND_CONTINUE;
          }
          return Evaluation.EXCLUDE_AND_PRUNE;
        }
      })
      .uniqueness(Uniqueness.NODE_GLOBAL)
      .traverse(topic);
  }

  private static void printTopic(@Nonnull Node topic) {
    System.out.println("Stored under <" + topic.getProperty(PROPERTY_VALUE) + ">: ");
    //first the topics
    System.out.println("--> Topics:");
    for (Path path : getSubTopics(topic)) {
      Node node = path.endNode();
      System.out.println("\t- " + getLabels(node) + ": " + toString(node));
    }

    System.out.println("--> Entries:");
    for (Path path : getSubEntries(topic)) {
      Node node = path.endNode();
      System.out.println("\t- " + getLabels(node) + ": " + toString(node));
    }
  }

  @Nonnull
  private Node findTopic(@Nonnull String value) {
    try (ResourceIterator<Node> iterator = graphDb.findNodesByLabelAndProperty(Types.TOPIC, PROPERTY_VALUE, value).iterator()) {
      assertThat(iterator.hasNext()).isTrue();
      return iterator.next();
    }
  }

  @Nonnull
  private Node createInformation(@Nonnull String key, @Nonnull String value) {
    Node node = graphDb.createNode(Types.INFORMATION);
    node.setProperty(PROPERTY_KEY, key);
    node.setProperty(PROPERTY_VALUE, value);
    return node;
  }

  private static void relates(Node topic1, Node topic2) {
    topic1.createRelationshipTo(topic2, Relations.RELATES_TO);
  }

  @Nonnull
  private static String getLabels(@Nonnull Node node) {
    return Joiner.on(", ").join(node.getLabels());
  }

  @Nonnull
  private static String toString(@Nonnull Node node) {
    if (node.hasLabel(Types.NOTE)) {
      return String.valueOf(node.getProperty(PROPERTY_TITLE));
    }
    if (node.hasLabel(Types.INFORMATION)) {
      return String.valueOf(node.getProperty(PROPERTY_KEY) + ":\t" + node.getProperty(PROPERTY_VALUE));
    }
    if (node.hasLabel(Types.TOPIC)) {
      return String.valueOf(node.getProperty(PROPERTY_VALUE));
    }

    return node.toString();
  }

  private static void toTopics(@Nonnull Node node, @Nonnull Node... topicNodes) {
    for (Node topicNode : topicNodes) {
      node.createRelationshipTo(topicNode, Relations.TO_TOPIC);
    }
  }

  @Nonnull
  private Node createNote(@Nonnull String title, @Nonnull String content) {
    Node node = graphDb.createNode(Types.NOTE);
    node.setProperty(PROPERTY_TITLE, title);
    node.setProperty(PROPERTY_CONTENT, content);
    return node;
  }

  @Nonnull
  private Node createTopic(@Nonnull String topicName, @Nonnull Node... parentTopics) {
    Node currentTopic = graphDb.createNode(Types.TOPIC);
    currentTopic.setProperty(PROPERTY_VALUE, topicName);

    for (Node parentTopic : parentTopics) {
      currentTopic.createRelationshipTo(parentTopic, Relations.TO_PARENT);
    }

    return currentTopic;
  }

  @Test
  public void testBasic() throws Exception {

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
      assertThat(graphDb.findNodesByLabelAndProperty(Types.INFORMATION, PROPERTY_URI, "http://www.google.de")).describedAs("google node").hasSize(1);
      assertThat(graphDb.findNodesByLabelAndProperty(Types.INFORMATION, PROPERTY_URI, "http://www.github.com")).describedAs("github node").hasSize(1);
      tx.success();
    }


  }

  public enum Types implements Label {
    TOPIC,
    INFORMATION,
    PASSWORD,
    RSA,
    MY_DATA,
    IDENTITY,
    NOTE,
  }

  public enum Relations implements RelationshipType {
    TO_PARENT,
    TO_TOPIC,
    RELATES_TO,
    AUTHENTICATION,
    CONTAINS

  }

}
