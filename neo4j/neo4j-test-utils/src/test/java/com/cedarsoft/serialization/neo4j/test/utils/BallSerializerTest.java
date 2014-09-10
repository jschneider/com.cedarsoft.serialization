package com.cedarsoft.serialization.neo4j.test.utils;

import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.ToString;
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
public class BallSerializerTest extends AbstractNeo4jSerializerTest2<Ball> {
  @Nonnull
  @Override
  protected BallSerializer getSerializer() throws Exception {
    return new BallSerializer();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create(
    new Ball.TennisBall( 7 ), BallSerializerTest.class.getResource( "ball1_2.cypher" )
  );

  @DataPoint
  public static final Entry<?> ENTRY2 = create(
    new Ball.BasketBall( "asdf" ), BallSerializerTest.class.getResource( "ball2_2.cypher" ) );


  @Test
  public void testAsccii() throws Exception {
    assertEquals( 2, getSerializer().getSerializingStrategySupport().getVersionMappings().getMappings().size() );
    assertEquals( "         -->  basketBa  tennisBa\n" +
                    "--------------------------------\n" +
                    "   1.0.0 -->     2.0.0     1.5.0\n" +
                    "   1.1.0 -->     2.0.1     1.5.1\n" +
                    "--------------------------------\n", VersionMappingsVisualizer.toString( getSerializer().getSerializingStrategySupport().getVersionMappings(), new ToString<SerializingStrategy<? extends Ball, Node, Node, IOException, Node, Node>>() {
      @Nonnull
      @Override
      public String convert( @Nonnull SerializingStrategy<? extends Ball, Node, Node, IOException, Node, Node> object ) {
        return object.getId();
      }
    } ) );
  }

  @Test
  public void testVersion() throws Exception {
    BallSerializer serializer = getSerializer();
    assertTrue( serializer.isVersionReadable( Version.valueOf( 1, 0, 0 ) ) );
    assertFalse( serializer.isVersionReadable( Version.valueOf( 1, 2, 1 ) ) );
    assertFalse( serializer.isVersionReadable( Version.valueOf( 0, 9, 9 ) ) );

    assertTrue( serializer.isVersionWritable( Version.valueOf( 1, 1, 0 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 1, 1, 1 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 1, 0, 9 ) ) );
  }
}