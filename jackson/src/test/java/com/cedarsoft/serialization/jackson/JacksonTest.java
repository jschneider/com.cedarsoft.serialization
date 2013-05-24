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

import com.cedarsoft.test.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;


/**
 *
 */
public class JacksonTest {
  private JsonFactory jsonFactory;
  private ByteArrayOutputStream out;
  private JsonGenerator generator;

  @Before
  public void setUp() throws Exception {
    jsonFactory = JacksonSupport.getJsonFactory();
    out = new ByteArrayOutputStream();
    generator = jsonFactory.createGenerator( out, JsonEncoding.UTF8 );
  }

  @Test
  public void testNull() throws Exception {
    generator.writeNull();
    verifyGenerator( "null" );

    JsonParser parser = jsonFactory.createJsonParser( "null" );
    assertEquals( null, parser.getCurrentToken() );
    assertEquals( JsonToken.VALUE_NULL, parser.nextToken() );
    assertEquals( null, parser.nextToken() );
  }

  @Test
  public void testBasic() throws Exception {
    generator.writeStartObject();
    generator.writeEndObject();
    verifyGenerator( "{}" );
  }

  @Test
  public void testBasic2() throws Exception {
    generator.writeStartObject();
    generator.writeFieldName( "daFieldName" );
    generator.writeString( "daStringValue" );
    generator.writeEndObject();
    verifyGenerator( "{\n" +
                       "  \"daFieldName\" : \"daStringValue\"\n" +
                       "}" );
  }

  @Test
  public void testArray() throws Exception {
    generator.writeStartObject();
    generator.writeFieldName( "daArray" );
    generator.writeStartArray();
    generator.writeString( "daContent1" );
    generator.writeString( "daContent2" );
    generator.writeEndArray();
    generator.writeEndObject();
    verifyGenerator( "{\n" +
                       "  \"daArray\" : [ \"daContent1\", \"daContent2\" ]\n" +
                       "}" );
  }

  @Test
  public void testRawArray() throws Exception {
    generator.writeStartArray();
    generator.writeString( "daContent1" );
    generator.writeString( "daContent2" );
    generator.writeEndArray();
    verifyGenerator( "[ \"daContent1\", \"daContent2\" ]" );
  }

  @Test
  public void testOnlyText() throws Exception {
    generator.writeString( "daContent1" );
    verifyGenerator( "\"daContent1\"" );
  }

  private void verifyGenerator( @Nonnull String control ) throws IOException {
    generator.flush();
    JsonUtils.assertJsonEquals(control, out.toString());
  }
}
