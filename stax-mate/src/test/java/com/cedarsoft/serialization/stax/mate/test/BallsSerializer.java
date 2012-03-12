package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.serialization.stax.mate.CollectionSerializer;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallsSerializer extends CollectionSerializer<Ball> {
  public BallsSerializer( @Nonnull AbstractStaxMateSerializer<Ball> ballAbstractStaxMateSerializer ) {
    super( Ball.class, ballAbstractStaxMateSerializer );
  }
}
