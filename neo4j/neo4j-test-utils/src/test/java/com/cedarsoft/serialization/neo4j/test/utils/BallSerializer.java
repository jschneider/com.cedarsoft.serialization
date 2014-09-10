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
public class BallSerializer extends AbstractDelegatingNeo4jSerializer<Ball> {
  public BallSerializer() {
    super( "http://test/ball", VersionRange.from( 1, 0, 0 ).to( 1, 1, 0 ) );

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

  /**
   *
   */
  public static class TennisBallSerializer extends AbstractNeo4jSerializingStrategy<Ball.TennisBall> {
    public TennisBallSerializer() {
      super( "tennisBall", "http://test/tennisball", Ball.TennisBall.class, VersionRange.from( 1, 5, 0 ).to( 1, 5, 1 ) );
    }


    @Override
    protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Ball.TennisBall object, @Nonnull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.setProperty( "newId", object.getId() );
    }

    @Nonnull
    @Override
    public Ball.TennisBall deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      verifyVersionReadable( formatVersion );

      int id;
      if ( formatVersion.equals( Version.valueOf( 1, 5, 0 ) ) ) {
        //legacy support
        id = ((Long) deserializeFrom.getProperty( "id" )).intValue();
      }else{
        //This is the new version
        id = ((Long) deserializeFrom.getProperty( "newId" )).intValue();
      }

      return new Ball.TennisBall( id );
    }
  }

  /**
   *
   */
  public static class BasketBallSerializer extends AbstractNeo4jSerializingStrategy<Ball.BasketBall> {
    public BasketBallSerializer() {
      super( "basketBall", "http://test/basketball", Ball.BasketBall.class, VersionRange.from( 2, 0, 0 ).to( 2, 0, 1 ) );
    }

    @Override
    protected void serializeInternal( @Nonnull Node serializeTo, @Nonnull Ball.BasketBall object, @Nonnull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.setProperty( "myNewId", object.getTheId() );
    }

    @Nonnull
    @Override
    public Ball.BasketBall deserialize( @Nonnull Node deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, IOException {
      verifyVersionReadable( formatVersion );

      String id;
      if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
        //Old version
        id = ( String ) deserializeFrom.getProperty( "id" );
      }else{
        id = ( String ) deserializeFrom.getProperty( "myNewId" );
      }


      return new Ball.BasketBall(  id );
    }
  }
}