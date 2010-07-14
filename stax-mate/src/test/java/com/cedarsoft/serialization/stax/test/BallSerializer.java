package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractDelegatingStaxMateSerializer;

/**
 *
 */
public class BallSerializer extends AbstractDelegatingStaxMateSerializer<Ball> {
  public BallSerializer() {
    super( "ball", "http://test/ball", VersionRange.from( 1, 0, 0 ).to( 1, 1, 0 ) );

    addStrategy( new TennisBallSerializer() )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 5, 0 )
      .map( 1, 1, 0 ).toDelegateVersion( 1, 5, 1 )
      ;

    addStrategy( new BasketBallSerializer() )
      .map( 1, 0, 0 ).toDelegateVersion( 2, 0, 0 )
      .map( 1, 1, 0 ).toDelegateVersion( 2, 0, 1 )
      ;

    getSerializingStrategySupport().verify();
  }
}
