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

import com.cedarsoft.version.Version;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.*;
import org.junit.experimental.theories.*;
import org.junit.runner.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
@RunWith( Theories.class )
public class IgnoringSerializerTest {

  private IgnoringSerializer serializer;

  @Before
  public void setUp() throws Exception {
    serializer = new IgnoringSerializer();
  }

  @Theory
  public void testIt( @Nonnull String json ) throws Exception {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    JsonParser parser = jsonFactory.createParser( new ByteArrayInputStream( json.getBytes() ) );

    Void result = serializer.deserialize( parser, Version.valueOf( 1, 0, 0 ) ); //we use the override stuff to avoid version/type check
    assertThat( result ).isNull();

    JsonToken nextToken = parser.nextToken();
    assertThat( nextToken ).isNull();
  }
  
  @DataPoints
  public static String[] testIt() throws Exception {
    return new String[]{
      "{}",
      "[]",
      "[1,2,3,4]",
      "{\"id\":123}",
      "{\"id\":[123]}",
      "{\"id\":{\"value\":123}}",
      "\"\""
    };
  }
}
