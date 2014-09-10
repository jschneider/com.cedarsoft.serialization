package com.cedarsoft.serialization.neo4j.test.utils;

import com.cedarsoft.serialization.neo4j.AbstractDelegatingNeo4jSerializer;
import com.cedarsoft.serialization.neo4j.AbstractNeo4jSerializingStrategy;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializerOld extends AbstractDelegatingNeo4jSerializer<Ball> {
  public BallSerializerOld() {
    super( "http://test/ball", VersionRange.single( 1, 0, 0 ) );

    addStrategy( new TennisBallSerializer() )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
    ;

    addStrategy( new BasketBallSerializer() )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
    ;

    getSerializingStrategySupport().verify();
  }

  /**
   *
   */
  public static class TennisBallSerializer extends AbstractNeo4jSerializingStrategy<Ball.TennisBall> {
    public TennisBallSerializer() {
      super( "tennisBall", "http://test/tennisball", Ball.TennisBall.class, VersionRange.single( 1, 0, 0 ) );
    }


    @Override
    protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Ball.TennisBall object, @Nonnull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.setProperty( "id", object.getId() );
    }

    @Nonnull
    @Override
    public Ball.TennisBall deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      verifyVersionReadable( formatVersion );

      int id = ((Long) deserializeFrom.getProperty( "id" )).intValue();
      return new Ball.TennisBall( id );
    }
  }

  /**
   *
   */
  public static class BasketBallSerializer extends AbstractNeo4jSerializingStrategy<Ball.BasketBall> {
    public BasketBallSerializer() {
      super( "basketBall", "http://test/basketball", Ball.BasketBall.class, VersionRange.single( 1, 0, 0 ) );
    }

    @Override
    protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Ball.BasketBall object, @Nonnull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.setProperty( "id", object.getTheId() );
    }

    @Nonnull
    @Override
    public Ball.BasketBall deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      verifyVersionReadable( formatVersion );

      String id = ( String ) deserializeFrom.getProperty( "id" );
      return new Ball.BasketBall(  id );
    }
  }
}