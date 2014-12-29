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
package com.cedarsoft.serialization.jackson.test.compatible;

import com.cedarsoft.serialization.jackson.JacksonSupport;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonParserWrapperTest {
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
  public void testStartObject() throws Exception {
    JacksonParserWrapper parser = new JacksonParserWrapper( jsonFactory.createJsonParser( "{}" ) );
    parser.startObject();
    try {
      parser.startObject();
      fail( "Where is the Exception" );
    } catch ( JsonParseException e ) {
      assertThat( e.getMessage() ).startsWith( "Invalid token. Expected <START_OBJECT> but got <END_OBJECT>" );
    }
  }

  @Test
  public void testEndObject() throws Exception {
    JacksonParserWrapper parser = new JacksonParserWrapper( jsonFactory.createJsonParser( "{}" ) );
    parser.startObject();
    parser.endObject();
    try {
      parser.endObject();
      fail( "Where is the Exception" );
    } catch ( JsonParseException e ) {
      assertThat( e.getMessage() ).startsWith( "Invalid token. Expected <END_OBJECT> but got <null>" );
    }
  }

  @Test
  public void testSimple() throws Exception {
    JacksonParserWrapper parser = new JacksonParserWrapper( jsonFactory.createJsonParser( getClass().getResource( "simple.json" ) ) );
    parser.startObject();

    parser.nextFieldValue( "street" );
    assertThat( parser.getText() ).isEqualTo( "Schlossalle" );

    parser.nextFieldValue( "number" );
    assertThat( parser.getIntValue() ).isEqualTo( 7 );

    parser.endObject();
  }
}
