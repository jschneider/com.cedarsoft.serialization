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

import com.cedarsoft.serialization.jackson.AbstractDelegatingJacksonSerializer;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializingStrategy;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializer extends AbstractDelegatingJacksonSerializer<Ball> {
  public BallSerializer() {
    super( "ball", VersionRange.from( 1, 0, 0 ).to( 1, 1, 0 ) );

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
    public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Ball.TennisBall object, @Nonnull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.writeNumberField( "id", object.getId() );
    }

    @Nonnull
    @Override
    public Ball.TennisBall deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException {
      JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );
      verifyVersionReadable( formatVersion );

      int id;
      if ( formatVersion.equals( Version.valueOf( 1, 5, 0 ) ) ) {
        parser.nextFieldValue( FIELD_NAME_DEFAULT_TEXT );
        id = deserializeFrom.getIntValue();
      } else {
        parser.nextFieldValue( "id" );
        id = deserializeFrom.getIntValue();
      }
      parser.closeObject();

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
    public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Ball.BasketBall object, @Nonnull Version formatVersion ) throws IOException {
      verifyVersionReadable( formatVersion );
      serializeTo.writeStringField( "theId", String.valueOf( object.getTheId() ) );
    }

    @Nonnull
    @Override
    public Ball.BasketBall deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException {
      verifyVersionReadable( formatVersion );

      JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );

      String theId;
      if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
        parser.nextFieldValue( FIELD_NAME_DEFAULT_TEXT );
        theId = parser.getText();
      } else {
        parser.nextFieldValue( "theId" );
        theId = parser.getText();
      }
      parser.closeObject();

      return new Ball.BasketBall( theId );
    }
  }
}
