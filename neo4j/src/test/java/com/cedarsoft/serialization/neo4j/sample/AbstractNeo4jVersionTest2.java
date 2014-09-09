package com.cedarsoft.serialization.neo4j.sample;

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializer;
import com.cedarsoft.serialization.neo4j.Neo4jRule;
import com.cedarsoft.serialization.test.utils.VersionEntry;
import com.cedarsoft.version.Version;
import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.experimental.theories.*;
import org.junit.runner.*;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
@RunWith( Theories.class )
public abstract class AbstractNeo4jVersionTest2<T> {
  @Rule
  public Neo4jRule neo4jRule = new Neo4jRule();

  /**
   * This method checks old serialized objects
   *
   * @throws IOException
   * @throws SAXException
   */
  @Theory
  public void testVersion( @Nonnull VersionEntry entry ) throws Exception {
    AbstractNeo4jSerializer<T> serializer = getSerializer();

    Version version = entry.getVersion();
    String serialized = new String( entry.getSerialized( serializer ), Charsets.UTF_8 );

    T deserialized = deserialize( serializer, serialized );
    verifyDeserialized( deserialized, version );
  }

  @Nonnull
  private T deserialize( @Nonnull AbstractNeo4jSerializer<T> serializer, @Nonnull String serialized ) throws IOException {
    GraphDatabaseService db = neo4jRule.createDb();

    //Fill the db initially
    try ( Transaction tx = db.beginTx() ) {
      ExecutionResult result = new ExecutionEngine( db ).execute( serialized );
      tx.success();
    }

    try ( Transaction tx = db.beginTx() ) {
      return serializer.deserialize( db.getNodeById( 0 ) );
    }
  }

  /**
   * Returns the serializer
   *
   * @return the serializer
   */
  @Nonnull
  protected abstract AbstractNeo4jSerializer<T> getSerializer() throws Exception;

  /**
   * Verifies the deserialized object.
   *
   * @param deserialized the deserialized object
   * @param version      the version
   */
  protected abstract void verifyDeserialized( @Nonnull T deserialized, @Nonnull Version version ) throws Exception;


  @Nonnull
  protected static VersionEntry create( @Nonnull Version version, @Nonnull String json ) {
    return new Neo4jVersionEntry( version, json );
  }

  @Nonnull
  protected static VersionEntry create( @Nonnull Version version, @Nonnull URL expected ) {
    try {
      return new Neo4jVersionEntry( version, IOUtils.toByteArray( expected.openStream() ) );
    } catch ( IOException e ) {
      throw new RuntimeException( e );
    }
  }

  public static class Neo4jVersionEntry implements VersionEntry {
    @Nonnull
    private final Version version;
    @Nonnull
    private final byte[] cypher;

    public Neo4jVersionEntry( @Nonnull Version version, @Nonnull String cypher ) {
      this( version, cypher.getBytes() );
    }

    public Neo4jVersionEntry( @Nonnull Version version, @Nonnull byte[] cypher ) {
      this.version = version;
      //noinspection AssignmentToCollectionOrArrayFieldFromParameter
      this.cypher = cypher;
    }

    @Nonnull
    @Override
    public Version getVersion() {
      return version;
    }

    @Nonnull
    @Override
    public byte[] getSerialized( @Nonnull Serializer<?, ?, ?> serializer ) throws Exception {
      return cypher;
    }
  }
}