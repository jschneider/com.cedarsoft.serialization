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

package com.cedarsoft.serialization.jackson;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.test.utils.JsonUtils;
import com.cedarsoft.version.Version;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.*;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 */
public class ListSerializerTest extends AbstractJsonSerializerTest2<List<? extends Object>> {
  @Override
  protected boolean addTypeInformation() {
    return false;
  }

  @Nonnull
  @Override
  protected ListSerializer getSerializer() throws Exception {
    return new ListSerializer();
  }

  @Override
  protected void verifyDeserialized( @Nonnull List<? extends Object> deserialized, @Nonnull List<? extends Object> original ) {
    assertEquals( original.size(), deserialized.size() );

    for ( int i = 0, deserialized1Size = deserialized.size(); i < deserialized1Size; i++ ) {
      Object deserializedElement = deserialized.get( i );
      Object originalElement = original.get( i );

      assertEquals( originalElement, deserializedElement );
    }
    assertThat( deserialized ).isEqualTo( original );
  }

  @Test
  public void testIt() throws Exception {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createGenerator( out, JsonEncoding.UTF8 );

    getSerializer().serialize( generator, Arrays.asList( "a", "b", "c", 42, 3.141, false, null ), Version.valueOf( 1, 0, 0 ) );

    generator.close();
    JsonUtils.assertJsonEquals("[ \"a\", \"b\", \"c\", 42, 3.141, false, null ]", out.toString());

    List<? extends Object> deserialized = getSerializer().deserialize( new ByteArrayInputStream( out.toByteArray() ) );
    assertEquals( 7, deserialized.size() );
    assertEquals( "a", deserialized.get( 0 ) );
    assertEquals( "b", deserialized.get( 1 ) );
    assertEquals( "c", deserialized.get( 2 ) );
    assertEquals( 42, deserialized.get( 3 ) );
    assertEquals( 3.141, deserialized.get( 4 ) );
    assertEquals( false, deserialized.get( 5 ) );
    assertEquals( null, deserialized.get( 6 ) );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( Arrays.asList( "a", "b", "c", 54, 234.0, 32.0, false ), "[ \"a\", \"b\", \"c\", 54, 234.0, 32.0, false ]" );

  @DataPoint
  public static final Entry<?> ONLY_STRINGS = create( Arrays.asList( "a", "b", "c" ), "[ \"a\", \"b\", \"c\" ]" );

}
