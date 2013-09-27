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

import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class RoomSerializerTest extends AbstractXmlSerializerTest<Room> {
  @Nonnull
  @Override
  protected String getExpectedSerialized() {
    return
      "<room>\n" +
        "  <description>descr</description>\n" +
        "    <window width=\"20.0\" height=\"30.0\">\n" +
        "      <description>asdf</description>\n" +
        "    </window>\n" +
        "    <window width=\"50.0\" height=\"60.7\">\n" +
        "      <description>asdf2</description>\n" +
        "    </window>\n" +
        "    <door>\n" +
        "      <description>asdf</description>\n" +
        "    </door>\n" +
        "    <door>\n" +
        "      <description>asdf2</description>\n" +
        "    </door>\n" +
        "    <door>\n" +
        "      <description>asdf3</description>\n" +
        "    </door>\n" +
        "</room>";
  }

  @Nonnull
  @Override
  protected Serializer<Room, OutputStream, InputStream> getSerializer() throws Exception {
    return new Room.Serializer( new Window.Serializer(), new Door.Serializer() );
  }

  @Nonnull
  @Override
  protected Room createObjectToSerialize() throws Exception {
    List<Window> windows = Arrays.asList( new Window( "asdf", 20, 30 ), new Window( "asdf2", 50, 60.7 ) );
    List<Door> doors = Arrays.asList( new Door( "asdf" ), new Door( "asdf2" ), new Door( "asdf3" ) );
    return new Room( "descr", windows, doors );
  }
}
