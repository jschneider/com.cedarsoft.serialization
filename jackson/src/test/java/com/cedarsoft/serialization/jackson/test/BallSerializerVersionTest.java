package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializerVersionTest extends AbstractJsonVersionTest2<Ball> {
  @NotNull
  @Override
  protected Serializer<Ball> getSerializer() throws Exception {
    return new BallSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull Ball deserialized, @NotNull Version version ) throws Exception {
    if ( deserialized instanceof Ball.BasketBall ) {
      assertEquals( "asdf", ( ( Ball.BasketBall ) deserialized ).getTheId() );
    }

    if ( deserialized instanceof Ball.TennisBall ) {
      assertEquals( 7, ( ( Ball.TennisBall ) deserialized ).getId() );
    }
  }

  @DataPoint
  public static final VersionEntry ENTRY2 = create( Version.valueOf( 1, 1, 0 ), "{\"@type\" : \"tennisBall\",\"id\" : 7}" );
  @DataPoint
  public static final VersionEntry ENTRY3 = create( Version.valueOf( 1, 1, 0 ), "{\"@type\" : \"basketBall\",\"theId\" : \"asdf\"}" );

  @DataPoint
  public static final VersionEntry ENTRY1 = create( Version.valueOf( 1, 0, 0 ), "{\"@type\" : \"tennisBall\",\"$\" : 7}" );
  @DataPoint
  public static final VersionEntry ENTRY4 = create( Version.valueOf( 1, 0, 0 ), "{\"@type\" : \"basketBall\",\"$\" : \"asdf\"}" );
}
