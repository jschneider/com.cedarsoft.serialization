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

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.apache.commons.codec.binary.Hex;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializerTest extends AbstractJsonSerializerTest2<User> {
  @Nonnull
  @Override
  protected StreamSerializer<User> getSerializer() throws Exception {
    return new UserSerializer( new EmailSerializer(), new RoleSerializer(), new UserDetailsSerializer() );
  }

  @DataPoint
  public static Entry<?> json() {
    return create( new User( "Max Mustermann",
                             Arrays.asList(
                               new Email( "test@test.de" ),
                               new Email( "other@test.de" )
                             ),
                             Arrays.asList(
                               new Role( 1, "Nobody" ),
                               new Role( 0, "Admin" )
                             ),
                             new Email( "single" ),
                             new UserDetails( 2351351L, 36351531153L, new String( Hex.encodeHex( "hash".getBytes() ) ).getBytes() )
    ), UserSerializerTest.class.getResource( "user.withDetails.json" ) );
  }

  @DataPoint
  public static Entry<?> noDetails() {
    return create( new User( "Max Mustermann",
                             Arrays.asList(
                               new Email( "test@test.de" ),
                               new Email( "other@test.de" )
                             ),
                             Arrays.asList(
                               new Role( 1, "Nobody" ),
                               new Role( 0, "Admin" )
                             ),
                             new Email( "single" )
    ), UserSerializerTest.class.getResource( "user.json" ) );
  }
}
