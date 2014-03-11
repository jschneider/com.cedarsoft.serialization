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

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.jackson.BooleanSerializer;
import com.cedarsoft.serialization.jackson.ByteSerializer;
import com.cedarsoft.serialization.jackson.CharacterSerializer;
import com.cedarsoft.serialization.jackson.DoubleSerializer;
import com.cedarsoft.serialization.jackson.FloatSerializer;
import com.cedarsoft.serialization.jackson.IntegerSerializer;
import com.cedarsoft.serialization.jackson.LongSerializer;
import com.cedarsoft.serialization.jackson.ShortSerializer;
import com.cedarsoft.serialization.jackson.StringSerializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

/**
 *
 */
public class PrimitivesTest extends AbstractJsonSerializerTest2<Primitives> {
  @Override
  protected boolean addTypeInformation() {
    return false;
  }

  @Nonnull
  @Override
  protected StreamSerializer<Primitives> getSerializer() throws Exception {
    return new PrimitivesSerializer( new IntegerSerializer(), new ShortSerializer(), new ByteSerializer(), new LongSerializer(), new DoubleSerializer(), new FloatSerializer(), new CharacterSerializer(), new BooleanSerializer(), new StringSerializer() );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( new Primitives( 1, ( short ) 2, ( byte ) 3, 4L, 5.5, 6.4f, '7', true, "asdf" ), PrimitivesTest.class.getResource( "primitives_1.0.0.json" ) );

}
