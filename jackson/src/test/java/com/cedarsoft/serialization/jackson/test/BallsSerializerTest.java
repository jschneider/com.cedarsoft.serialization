package com.cedarsoft.serialization.jackson.test;

import java.util.List;

import javax.annotation.Nonnull;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import com.google.common.collect.ImmutableList;

import org.assertj.core.api.Assertions;
import org.junit.experimental.theories.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallsSerializerTest extends AbstractJsonSerializerTest2<List<? extends Ball>> {
  @Nonnull
  @Override
  protected StreamSerializer<List<? extends Ball>> getSerializer() throws Exception {
    return new BallsSerializer( new BallSerializer() );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create(
    ImmutableList.<Ball>of(
      new Ball.TennisBall(7),
      new Ball.BasketBall("asdf")
    ), BallsSerializerTest.class.getResource("balls.json"));

  @Override
  protected void verifyDeserialized( @Nonnull List<? extends Ball> deserialized, @Nonnull List<? extends Ball> original ) {
    Assertions.assertThat(deserialized).isEqualTo( original );
  }
}