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

package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorSerializerVersionTest extends AbstractXmlVersionTest<Color> {
  @NotNull
  @Override
  protected Serializer<Color> getSerializer() throws Exception {
    return new ColorSerializer();
  }

  @NotNull
  @Override
  protected Map<? extends Version, ? extends String> getSerializedXml() {
    Map<Version, String> map = new HashMap<Version, String>();
    map.put( Version.valueOf( 1, 0, 0 ),
             "<color>\n" +
               "  <red>100</red>\n" +
               "  <green>42</green>\n" +
               "  <blue>130</blue>\n" +
               "</color>" );
    return map;
  }

  @Override
  protected void verifyDeserialized( @NotNull Color deserialized, @NotNull Version version ) throws Exception {
    Assert.assertEquals( deserialized.getRed(), 100 );
    Assert.assertEquals( deserialized.getGreen(), 42 );
    Assert.assertEquals( deserialized.getBlue(), 130 );
  }
}
