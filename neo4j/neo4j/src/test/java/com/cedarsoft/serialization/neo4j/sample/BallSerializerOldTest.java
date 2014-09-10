package com.cedarsoft.serialization.neo4j.sample;

import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.ToString;
import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializer;
import com.cedarsoft.serialization.test.utils.Entry;
import com.cedarsoft.serialization.ui.VersionMappingsVisualizer;
import com.cedarsoft.version.Version;
import org.junit.*;
import org.junit.experimental.theories.*;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializerOldTest extends AbstractNeo4jSerializerTest2<Ball> {
  @Nonnull
  @Override
  protected AbstractNeo4jSerializer<Ball> getSerializer() throws Exception {
    return new BallSerializerOld();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create(
    new Ball.TennisBall( 7 ), BallSerializerOldTest.class.getResource( "ball1.cypher" )
  );

  @DataPoint
  public static final Entry<?> ENTRY2 = create(
    new Ball.BasketBall( "asdf" ), BallSerializerOldTest.class.getResource( "ball2.cypher" ) );
}