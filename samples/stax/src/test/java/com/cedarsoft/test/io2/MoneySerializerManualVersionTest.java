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

import com.cedarsoft.test.Money;
import com.cedarsoft.test.utils.AssertUtils;
import org.junit.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class that tests the old versions manually
 */
public class MoneySerializerManualVersionTest {
  @Test
  public void testCurrent() throws IOException, SAXException {
    MoneySerializer serializer = new MoneySerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Money( 7, 99 ), out );

    AssertUtils.assertXMLEquals(out.toString(), "<money xmlns=\"http://thecompany.com/test/money/1.0.1\" cents=\"799\" />");

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), new Money( 7, 99 ) );
  }

  @Test
  public void testOldFormat() throws IOException {
    MoneySerializer serializer = new MoneySerializer();
    assertEquals( serializer.deserialize( new ByteArrayInputStream( ( "<money xmlns=\"http://thecompany.com/test/money/1.0.0\">799</money>" ).getBytes() ) ), new Money( 7, 99 ) );
  }

  @Test
  public void testCurrentFormat() throws IOException {
    MoneySerializer serializer = new MoneySerializer();
    assertEquals( serializer.deserialize( new ByteArrayInputStream( ( "<money xmlns=\"http://thecompany.com/test/money/1.0.1\" cents=\"799\" />" ).getBytes() ) ), new Money( 7, 99 ) );
  }
}
