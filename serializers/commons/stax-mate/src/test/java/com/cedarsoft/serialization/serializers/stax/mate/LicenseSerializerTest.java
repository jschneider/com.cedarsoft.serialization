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

package com.cedarsoft.serialization.serializers.stax.mate;

import com.cedarsoft.license.License;
import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

/**
 *
 */
public class LicenseSerializerTest extends AbstractXmlSerializerTest2<License> {
  @Nonnull
  @Override
  protected StreamSerializer<License> getSerializer() {
    return new LicenseSerializer();
  }

  @DataPoint
  public static final Entry<?> entry1 = create(
    License.PUBLIC_DOMAIN,
    "<license id=\"PUBLIC_DOMAIN\">\n" +
      "  <name>Public Domain</name>\n" +
      "</license>" );

  @DataPoint
  public static final Entry<?> entry2 = create(
    License.ALL_RIGHTS_RESERVED,
    "<license id=\"ALL_RIGHTS_RESERVED\">\n" +
      "  <name>All rights reserved</name>\n" +
      "</license>" );

  @DataPoint
  public static final Entry<?> entry3 = create(
    License.UNKNOWN,
    "<license id=\"UNKNOWN\">\n" +
      "  <name>Unknown</name>\n" +
      "</license>" );
}
