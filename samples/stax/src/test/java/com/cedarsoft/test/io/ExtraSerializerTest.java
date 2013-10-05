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

package com.cedarsoft.test.io;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerMultiTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ExtraSerializerTest extends AbstractXmlSerializerMultiTest<Extra> {
  @Nonnull
  @Override
  protected StreamSerializer<Extra> getSerializer() {
    //This serializer uses a delegate
    return new ExtraSerializer( new MoneySerializer() );
  }

  @Nonnull
  @Override
  protected Iterable<? extends Extra> createObjectsToSerialize() {
    return Arrays.asList(
      new Extra( "Metallic", new Money( 400, 00 ) ),
      new Extra( "Great Radio", new Money( 700, 00 ) )
    );
  }

  @Nonnull
  @Override
  protected List<? extends String> getExpectedSerialized() throws Exception {
    return Arrays.asList(
      "<extra>\n" +
        "  <description>Metallic</description>\n" +
        "  <price>40000</price>\n" +
        "</extra>",
      "<extra>\n" +
        "  <description>Great Radio</description>\n" +
        "  <price>70000</price>\n" +
        "</extra>" );
  }
}
