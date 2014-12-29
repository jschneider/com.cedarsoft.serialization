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
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 *
 */
public class StringSerializerTest extends AbstractJsonSerializerTest2<String> {
  @Override
  protected boolean addTypeInformation() {
    return false;
  }

  @Nonnull
  @Override
  protected StringSerializer getSerializer() throws Exception {
    return new StringSerializer();
  }

  @Test
  public void testNotClose() throws Exception {
    final boolean[] shallAcceptClose = {false};
    final boolean[] closed = new boolean[1];

    OutputStream out = new FilterOutputStream( new ByteArrayOutputStream() ) {

      @Override
      public void close() throws IOException {
        if ( !shallAcceptClose[0] ) {
          fail( "Unacceptable close!" );
        }

        super.close();
        closed[0] = true;
      }
    };

    getSerializer().serialize( "daString", out );
    shallAcceptClose[0] = true;
    out.close();
    assertThat( closed[0] ).isTrue();
  }

  @Test
  public void testIt() throws Exception {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createGenerator( out, JsonEncoding.UTF8 );

    getSerializer().serialize( generator, "asdf", Version.valueOf( 1, 0, 0 ) );

    generator.close();
    JsonUtils.assertJsonEquals("\"asdf\"", out.toString());
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( "asdf", "\"asdf\"" );
}
