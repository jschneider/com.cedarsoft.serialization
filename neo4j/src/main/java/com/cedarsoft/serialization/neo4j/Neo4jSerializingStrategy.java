package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.SerializingStrategy;
import org.neo4j.graphdb.Node;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface Neo4jSerializingStrategy<T> extends SerializingStrategy<T, Node, Node, IOException, Node, Node> {
}
