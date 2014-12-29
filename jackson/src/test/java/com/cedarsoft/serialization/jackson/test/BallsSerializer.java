package com.cedarsoft.serialization.jackson.test;

import javax.annotation.Nonnull;

import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.CollectionSerializer;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallsSerializer extends CollectionSerializer<Ball>{
  public BallsSerializer( @Nonnull AbstractJacksonSerializer<Ball> serializer) {
    super(Ball.class, serializer);
  }
}
