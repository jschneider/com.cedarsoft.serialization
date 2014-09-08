package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.AbstractSerializer;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for neo4j serializers
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNeo4jSerializer<T> extends AbstractSerializer<T, Node, Node, IOException, Node, Node> {
  /**
   * This property contains the format version for a node
   */
  @Nonnull
  public static final String PROPERTY_FORMAT_VERSION = "formatVersion";

  @Nonnull
  private final String type; //$NON-NLS-1$

  protected AbstractNeo4jSerializer( @Nonnull String type, @Nonnull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.type = type;
  }

  @Override
  public final void serialize( @Nonnull T object, @Nonnull Node out ) throws IOException {
    serialize( out, object, getFormatVersion() );
  }

  @Override
  public final void serialize( @Nonnull Node serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws VersionException, IOException {
    verifyVersionWritable( formatVersion );

    serializeTo.addLabel(getTypeLabel());
    addVersion( serializeTo );

    serializeInternal( serializeTo, object, formatVersion );
  }

  /**
   * Adds the type and version
   * @param serializeTo the node the type and version is added to
   */
  protected void addVersion( @Nonnull Node serializeTo ) {
    serializeTo.setProperty( PROPERTY_FORMAT_VERSION, getFormatVersion().toString() );
  }

  /**
   * This method must be implemented by sub classes. Serialize the custom fields when necessary.<br/>
   * This method is called from {@link #serialize(Node, Object, Version)}. The type label and format version have already been added to the node
   * @param serializeTo the node to serialize to
   * @param object the object
   * @param formatVersion the format version
   * @throws IOException
   */
  protected abstract void serializeInternal( @Nonnull Node serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException;

  /**
   * Returns the type label
   * @return the type label
   */
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

    Version version = readVersion( in );
    verifyVersionReadable(version);

    return deserialize(in, version);
  }

  @Nonnull
  protected Version readVersion( @Nonnull Node in ) {
    return Version.parse( ( String ) in.getProperty( PROPERTY_FORMAT_VERSION ) );
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
    node.createRelationshipTo( targetNode, relationshipType );
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
    public InvalidTypeException( @Nullable Label expectedLabel, @Nonnull Iterable<Label> expected ) {
      super( "Invalid type. Expected <" + expectedLabel + "> but found <" + Iterables.toString(expected) + ">" );
    }
  }
}
