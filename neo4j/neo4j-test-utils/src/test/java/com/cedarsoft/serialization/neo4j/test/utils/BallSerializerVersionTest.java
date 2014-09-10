package com.cedarsoft.serialization.neo4j.test.utils;

import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializer;
import com.cedarsoft.serialization.test.utils.VersionEntry;
import com.cedarsoft.version.Version;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializerVersionTest extends AbstractNeo4jVersionTest2<Ball> {
  @Nonnull
  @Override
  protected AbstractNeo4jSerializer<Ball> getSerializer() throws Exception {
    return new BallSerializer();
  }

  @Override
  protected void verifyDeserialized( @Nonnull Ball deserialized, @Nonnull Version version ) throws Exception {
    if ( deserialized instanceof Ball.BasketBall ) {
      assertEquals( "asdf", ( ( Ball.BasketBall ) deserialized ).getTheId() );
    }

    if ( deserialized instanceof Ball.TennisBall ) {
      assertEquals( 7, ( ( Ball.TennisBall ) deserialized ).getId() );
    }
  }

  @DataPoint
  public static final VersionEntry ENTRY2 = create( Version.valueOf( 1, 1, 0 ), BallSerializerVersionTest.class.getResource( "ball1.cypher" ) );
  @DataPoint
  public static final VersionEntry ENTRY3 = create( Version.valueOf( 1, 1, 0 ), BallSerializerVersionTest.class.getResource( "ball1_2.cypher" ) );

  @DataPoint
  public static final VersionEntry ENTRY1 = create( Version.valueOf( 1, 0, 0 ), BallSerializerVersionTest.class.getResource( "ball2.cypher" ) );
  @DataPoint
  public static final VersionEntry ENTRY4 = create( Version.valueOf( 1, 0, 0 ), BallSerializerVersionTest.class.getResource( "ball2_2.cypher" ) );
}