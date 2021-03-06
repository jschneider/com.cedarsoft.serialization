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

package com.cedarsoft.serialization.test.utils;

import com.cedarsoft.serialization.AbstractXmlSerializer;
import com.cedarsoft.test.utils.AssertUtils;
import org.apache.commons.io.Charsets;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Abstract base class for XML based serializers.
 *
 * @param <T> the type of the serialized object
 * @deprecated use AbstractXmlSerializerTest2 instead
 */
@Deprecated
public abstract class AbstractXmlSerializerTest<T> extends AbstractSerializerTest<T> {
  @Override
  protected void verifySerialized( @Nonnull byte[] serialized ) throws Exception {
    String expectedWithNamespace = AbstractXmlSerializerTest2.addNameSpace( (AbstractXmlSerializer<?, ?, ?, ?>) getSerializer(), getExpectedSerialized().getBytes(StandardCharsets.UTF_8) );
    AssertUtils.assertXMLEquals(new String(serialized, getEncoding() ), expectedWithNamespace);
  }

  @Nonnull
  protected Charset getEncoding() {
    return Charsets.UTF_8;
  }

  /**
   * Returns the expected serialized string
   *
   * @return the expected serialized string
   */
  @Nonnull

  protected abstract String getExpectedSerialized();
}
