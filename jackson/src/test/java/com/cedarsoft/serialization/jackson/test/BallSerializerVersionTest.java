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

import com.cedarsoft.version.Version;
import com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.test.utils.VersionEntry;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BallSerializerVersionTest extends AbstractJsonVersionTest2<Ball> {
  @Nonnull
  @Override
  protected Serializer<Ball, OutputStream, InputStream> getSerializer() throws Exception {
    return new BallSerializer();
  }

  @Override
  protected void verifyDeserialized( @Nonnull Ball deserialized, @Nonnull Version version ) throws Exception {
    if ( deserialized instanceof Ball.BasketBall ) {
      assertEquals( "asdf", ( ( Ball.BasketBall ) deserialized ).getTheId() );
    }

    if ( deserialized instanceof Ball.TennisBall ) {
      assertEquals( 7, ( ( Ball.TennisBall ) deserialized ).getId() );
    }
  }

  @DataPoint
  public static final VersionEntry ENTRY2 = create( Version.valueOf( 1, 1, 0 ), "{\"@subtype\" : \"tennisBall\",\"id\" : 7}" );
  @DataPoint
  public static final VersionEntry ENTRY3 = create( Version.valueOf( 1, 1, 0 ), "{\"@subtype\" : \"basketBall\",\"theId\" : \"asdf\"}" );

  @DataPoint
  public static final VersionEntry ENTRY1 = create( Version.valueOf( 1, 0, 0 ), "{\"@subtype\" : \"tennisBall\",\"$\" : 7}" );
  @DataPoint
  public static final VersionEntry ENTRY4 = create( Version.valueOf( 1, 0, 0 ), "{\"@subtype\" : \"basketBall\",\"$\" : \"asdf\"}" );
}
