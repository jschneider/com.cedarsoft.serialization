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

import com.cedarsoft.JsonUtils;
import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

/**
 *
 */
public class StringCollectionSerializerTest extends AbstractJsonSerializerTest2<Collection<? extends String>> {
  @Override
  protected boolean addNameSpace() {
    return false;
  }

  @NotNull
  @Override
  protected StringCollectionSerializer getSerializer() throws Exception {
    return new StringCollectionSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull Collection<? extends String> deserialized, @NotNull Collection<? extends String> original ) {
    assertThat( deserialized ).isEqualTo( original );
  }

  @Test
  public void testIt() throws Exception {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );

    getSerializer().serialize( generator, Arrays.asList( "a", "b", "c" ), Version.valueOf( 1, 0, 0 ) );

    generator.close();
    JsonUtils.assertJsonEquals( "[ \"a\", \"b\", \"c\" ]", out.toString() );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( Arrays.asList( "a", "b", "c" ), "[ \"a\", \"b\", \"c\" ]" );
}
