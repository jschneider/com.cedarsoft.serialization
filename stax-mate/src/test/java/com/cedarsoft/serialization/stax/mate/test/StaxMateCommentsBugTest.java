package com.cedarsoft.serialization.stax.mate.test;

import org.junit.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StaxMateCommentsBugTest {
  @Test
  public void testManualWithComment() throws Exception {
    BallsSerializer serializer = new BallsSerializer(new BallSerializer());
    serializer.deserialize(BallsSerializerTest.class.getResourceAsStream("/com/cedarsoft/serialization/stax/mate/test/ball_with_comment.xml"));
  }
}
