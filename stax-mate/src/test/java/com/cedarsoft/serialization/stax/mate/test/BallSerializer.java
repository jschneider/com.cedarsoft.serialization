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

package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractDelegatingStaxMateSerializer;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializingStrategy;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

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

  /**
   *
   */
  public static class TennisBallSerializer extends AbstractStaxMateSerializingStrategy<Ball.TennisBall> {
    public TennisBallSerializer() {
      super( "tennisBall", "http://test/tennisball", Ball.TennisBall.class, VersionRange.from( 1, 5, 0 ).to( 1, 5, 1 ) );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Ball.TennisBall object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      verifyVersionReadable( formatVersion );
      serializeTo.addAttribute( "id", String.valueOf( object.getId() ) );
    }

    @Nonnull
    @Override
    public Ball.TennisBall deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      verifyVersionReadable( formatVersion );

      int id;
      if ( formatVersion.equals( Version.valueOf( 1, 5, 0 ) ) ) {
        id = Integer.parseInt( getText( deserializeFrom ) );
      } else {
        id = Integer.parseInt( deserializeFrom.getAttributeValue( null, "id" ) );
        closeTag( deserializeFrom );
      }

      return new Ball.TennisBall( id );
    }
  }

  /**
   *
   */
  public static class BasketBallSerializer extends AbstractStaxMateSerializingStrategy<Ball.BasketBall> {
    public BasketBallSerializer() {
      super( "basketBall", "http://test/basketball", Ball.BasketBall.class, VersionRange.from( 2, 0, 0 ).to( 2, 0, 1 ) );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Ball.BasketBall object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      verifyVersionReadable( formatVersion );
      serializeTo.addAttribute( "theId", String.valueOf( object.getTheId() ) );
    }

    @Nonnull
    @Override
    public Ball.BasketBall deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      verifyVersionReadable( formatVersion );

      String theId;
      if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
        theId = getText( deserializeFrom );
      } else {
        theId = deserializeFrom.getAttributeValue( null, "theId" );
        closeTag( deserializeFrom );
      }

      return new Ball.BasketBall( theId );
    }
  }
}
