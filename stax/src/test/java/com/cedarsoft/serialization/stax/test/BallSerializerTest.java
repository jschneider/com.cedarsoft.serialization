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

package com.cedarsoft.serialization.stax.test;

import com.google.common.collect.Lists;
import org.junit.*;
import org.junit.rules.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.cedarsoft.AssertUtils.assertXMLEquals;
import static org.junit.Assert.*;

/**
 *
 */
public class BallSerializerTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testIt() throws Exception {
    BallSerializer serializer = new BallSerializer();
    serializer.registerElementsSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Ball( 77, Lists.newArrayList( new Ball.Element( "a" ), new Ball.Element( "b" ) ) ), out );

    assertXMLEquals( getClass().getResource( "ball.xml" ), out.toString() );
    assertEquals( 77, serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ).getId() );
  }

  @Test
  public void testMissing() throws IOException {
    BallSerializer serializer = new BallSerializer();

    expectedException.expect( IllegalArgumentException.class );
    serializer.serialize( new Ball( 77, Lists.newArrayList( new Ball.Element( "a" ), new Ball.Element( "b" ) ) ), new ByteArrayOutputStream() );
  }
}
