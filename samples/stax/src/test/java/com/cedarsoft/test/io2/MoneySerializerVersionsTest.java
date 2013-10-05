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

package com.cedarsoft.test.io2;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Money;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class MoneySerializerVersionsTest extends AbstractXmlVersionTest<Money> {
  @Nonnull
  @Override
  protected MoneySerializer getSerializer() {
    return new MoneySerializer();
  }

  @Nonnull
  @Override
  protected Map<? extends Version, ? extends String> getSerializedXml() {
    Map<Version, String> map = new HashMap<Version, String>();

    //We don't have to add the namespace containing the version. This is done automatically
    map.put( new Version( 1, 0, 1 ), "<money cents=\"799\" />" );
    map.put( new Version( 1, 0, 0 ), "<money>799</money>" );

    return map;
  }

  @Override
  protected void verifyDeserialized( @Nonnull Money deserialized, @Nonnull Version version ) {
    assertEquals( new Money( 7, 99 ), deserialized );
  }
}
