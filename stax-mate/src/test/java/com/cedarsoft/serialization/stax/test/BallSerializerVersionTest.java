package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractXmlVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import static org.junit.Assert.*;

/**
 *
 */
public class BallSerializerVersionTest extends AbstractXmlVersionTest2<Ball> {
  @NotNull
  @Override
  protected Serializer<Ball> getSerializer() throws Exception {
    return new BallSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull Ball deserialized, @NotNull Version version ) throws Exception {
    if ( deserialized instanceof BasketBall ) {
      assertEquals( "asdf", ( ( BasketBall ) deserialized ).getTheId() );
    }

    if ( deserialized instanceof TennisBall ) {
      assertEquals( 7, ( ( TennisBall ) deserialized ).getId() );
    }
  }

  @DataPoint
  public static final VersionEntry ENTRY1 = create( Version.valueOf( 1, 0, 0 ), "<ball type=\"basketBall\">asdf</ball>" );
  @DataPoint
  public static final VersionEntry ENTRY4 = create( Version.valueOf( 1, 0, 0 ), "<ball type=\"tennisBall\">7</ball>" );

  @DataPoint
  public static final VersionEntry ENTRY2 = create( Version.valueOf( 1, 1, 0 ), "<ball type=\"tennisBall\" id=\"7\" />" );
  @DataPoint
  public static final VersionEntry ENTRY3 = create( Version.valueOf( 1, 1, 0 ), "<ball type=\"basketBall\" theId=\"asdf\"/>" );
}
