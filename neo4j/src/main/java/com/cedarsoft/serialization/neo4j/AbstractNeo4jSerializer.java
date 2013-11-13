package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.AbstractSerializer;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.google.common.collect.Iterables;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNeo4jSerializer<T> extends AbstractSerializer<T, Node, Node, IOException, Node, Node> implements Serializer<T, Node, Node> {
  @Nonnull
  public static final String PROPERTY_FORMAT_VERSION = "formatVersion";

  @Nonnull
  private final String type; //$NON-NLS-1$

  protected AbstractNeo4jSerializer( @Nonnull String type, @Nonnull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.type = type;
  }

  @Override
  public void serialize( @Nonnull T object, @Nonnull Node out ) throws IOException {
    serialize( out, object, getFormatVersion() );
  }

  @Override
  public void serialize( @Nonnull Node serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
    serializeTo.addLabel(getTypeLabel());
    serializeTo.setProperty( PROPERTY_FORMAT_VERSION, getFormatVersion().toString() );
  }

  @Nonnull
  public Label getTypeLabel() {
    return DynamicLabel.label(type);
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull Node in ) throws IOException, VersionException {
    try {
      verifyType(in);
    } catch (InvalidTypeException e) {
      throw new IOException("Could not parse due to " + e.getMessage(), e);
    }

    Version version = Version.parse((String) in.getProperty(PROPERTY_FORMAT_VERSION));
    verifyVersionReadable(version);

    return deserialize(in, version);
  }

  private void verifyType(@Nonnull Node in) throws InvalidTypeException {
    if (!in.hasLabel(getTypeLabel())) {
      throw new InvalidTypeException( getTypeLabel(), in.getLabels() );
    }
  }

  public <T> void serializeWithRelationships( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull Node node, @Nonnull RelationshipType relationshipType, @Nonnull Version formatVersion ) throws IOException {
    for ( T object : objects ) {
      serializeWithRelationship( object, type, node, relationshipType, formatVersion );
    }
  }

  /**
   * Serializes the given object using a relation
   *
   * @param object           the object that is serialized
   * @param type             the type
   * @param node             the (current) node that is the start for the relationship
   * @param relationshipType the type of the relationship
   * @param formatVersion    the format version
   * @param <T>              the type
   * @throws IOException
   */
  public <T> void serializeWithRelationship( @Nonnull T object, @Nonnull Class<T> type, @Nonnull Node node, @Nonnull RelationshipType relationshipType, @Nonnull Version formatVersion ) throws IOException {
    Node targetNode = node.getGraphDatabase().createNode();
    Relationship relationshipTo = node.createRelationshipTo( targetNode, relationshipType );
    serialize( object, type, targetNode, formatVersion );
  }

  @Nonnull
  public <T> T deserializeWithRelationship( @Nonnull Class<T> type, @Nonnull RelationshipType relationshipType, @Nonnull Node node, @Nonnull Version formatVersion ) throws IOException {
    @Nullable Relationship relationship = node.getSingleRelationship( relationshipType, Direction.OUTGOING );
    assert relationship != null;
    return deserialize( type, formatVersion, relationship.getEndNode() );
  }

  @Nonnull
  public <T> List<? extends T> deserializeWithRelationships( @Nonnull Class<T> type, @Nonnull RelationshipType relationshipType, @Nonnull Node node, @Nonnull Version formatVersion ) throws IOException {
    List<T> deserializedList = new ArrayList<>();
    for ( Relationship relationship : node.getRelationships( relationshipType, Direction.OUTGOING ) ) {
      deserializedList.add( deserialize( type, formatVersion, relationship.getEndNode() ) );
    }

    return deserializedList;
  }

  public static class InvalidTypeException extends Exception {
    public InvalidTypeException( @Nullable Label expectedLabel, @Nonnull ResourceIterable<Label> expected ) {
      super( "Invalid type. Expected <" + expectedLabel + "> but found <" + Iterables.toString(expected) + ">" );
    }
  }
}
