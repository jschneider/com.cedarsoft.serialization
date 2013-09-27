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

import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import com.cedarsoft.test.utils.AssertUtils;
import org.apache.commons.io.Charsets;
import org.junit.*;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;

/**
 *
 */
public class HashSerializerTest extends AbstractXmlSerializerTest2<Hash> {
  @Nonnull
  @Override
  protected HashSerializer getSerializer() {
    return new HashSerializer();
  }

  @DataPoint
  public static final Entry<?> entry1 = create( Hash.fromHex( Algorithm.SHA256, "11223344" ), "<hash algorithm=\"SHA256\">11223344</hash>" );

  @Test
  public void testIt() throws Exception {
    byte[] serialized = getSerializer().serializeToByteArray( Hash.fromHex( Algorithm.MD5, "121212" ) );
    AssertUtils.assertXMLEquals( new String( serialized, Charsets.UTF_8 ).trim(), "<hash xmlns=\"http://www.cedarsoft.com/crypt/hash/1.0.0\" algorithm=\"MD5\">121212</hash>" );

    Hash deserialized = getSerializer().deserialize( new ByteArrayInputStream( serialized ) );
    Assert.assertEquals( deserialized, Hash.fromHex( Algorithm.MD5, "121212" ) );
  }
}
