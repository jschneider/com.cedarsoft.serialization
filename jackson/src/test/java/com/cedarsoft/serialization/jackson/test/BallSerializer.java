/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractDelegatingJacksonSerializer;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializingStrategy;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializer extends AbstractDelegatingJacksonSerializer<Ball> {
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
  public static class TennisBallSerializer extends AbstractJacksonSerializingStrategy<Ball.TennisBall> {
    public TennisBallSerializer() {
      super( "tennisBall", "http://test/tennisball", Ball.TennisBall.class, VersionRange.from( 1, 5, 0 ).to( 1, 5, 1 ) );
    }

    @Override
    public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Ball.TennisBall object, @NotNull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.writeNumberField( "id", object.getId() );
    }

    @NotNull
    @Override
    public Ball.TennisBall deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException {
      verifyVersionReadable( formatVersion );

      int id;
      if ( formatVersion.equals( Version.valueOf( 1, 5, 0 ) ) ) {
        nextField( deserializeFrom, FIELD_NAME_DEFAULT_TEXT );
        id = deserializeFrom.getIntValue();
      } else {
        nextField( deserializeFrom, "id" );
        id = deserializeFrom.getIntValue();
      }
      closeObject( deserializeFrom );

      return new Ball.TennisBall( id );
    }
  }

  /**
   *
   */
  public static class BasketBallSerializer extends AbstractJacksonSerializingStrategy<Ball.BasketBall> {
    public BasketBallSerializer() {
      super( "basketBall", "http://test/basketball", Ball.BasketBall.class, VersionRange.from( 2, 0, 0 ).to( 2, 0, 1 ) );
    }

    @Override
    public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Ball.BasketBall object, @NotNull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.writeStringField( "theId", String.valueOf( object.getTheId() ) );
    }

    @NotNull
    @Override
    public Ball.BasketBall deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException {
      verifyVersionReadable( formatVersion );

      String theId;
      if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
        nextField( deserializeFrom, FIELD_NAME_DEFAULT_TEXT );
        theId = deserializeFrom.getText();
      } else {
        nextField( deserializeFrom, "theId" );
        theId = deserializeFrom.getText();
      }
      closeObject( deserializeFrom );

      return new Ball.BasketBall( theId );
    }
  }
}
