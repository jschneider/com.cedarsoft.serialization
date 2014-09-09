package com.cedarsoft.serialization.neo4j.test;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public enum Relations implements RelationshipType {
  MARRIED,
  SON,
  DAUGHTER
  ;
}
