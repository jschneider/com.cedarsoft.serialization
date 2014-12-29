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

package com.cedarsoft.serialization.test.performance;

import com.cedarsoft.test.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;
import org.apache.commons.codec.binary.Hex;
import org.assertj.core.api.Assertions;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Bson4JacksonTest {

  public static final String BSON = "00000000026964000a00000043616e6f6e205261770008646570656e64656e74000003657874656e73696f6e000000000002657874656e73696f6e0004000000637232000864656661756c7400010264656c696d6974657200020000002e000000";
  private BsonFactory jsonFactory;

  @Before
  public void setUp() throws Exception {
    jsonFactory = new BsonFactory();
    jsonFactory.enable( BsonGenerator.Feature.ENABLE_STREAMING );
  }

  @Test
  public void testMapper() throws Exception {
    ObjectMapper mapper = new ObjectMapper( jsonFactory );

    com.cedarsoft.serialization.test.performance.jaxb.FileType fileType = new com.cedarsoft.serialization.test.performance.jaxb.FileType( "Canon Raw", new com.cedarsoft.serialization.test.performance.jaxb.Extension( ".", "cr2", true ), false );
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    mapper.writeValue( out, fileType );

    Assertions.assertThat( Hex.encodeHexString( out.toByteArray() ) ).describedAs( out.toString() ).isEqualTo( "0000000008646570656e64656e740000026964000a00000043616e6f6e205261770003657874656e73696f6e00000000000264656c696d6974657200020000002e0002657874656e73696f6e0004000000637232000864656661756c7400010000" );
  }

  @Test
  public void testIt() throws IOException {
    FileType fileType = new FileType( "Canon Raw", new Extension( ".", "cr2", true ), false );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createGenerator( out, JsonEncoding.UTF8 );

    generator.writeStartObject();
    generator.writeStringField( "id", fileType.getId() );
    generator.writeBooleanField( "dependent", fileType.isDependent() );

    generator.writeFieldName( "extension" );
    generator.writeStartObject();

    generator.writeStringField( "extension", fileType.getExtension().getExtension() );
    generator.writeBooleanField( "default", fileType.getExtension().isDefault() );
    generator.writeStringField( "delimiter", fileType.getExtension().getDelimiter() );

    generator.writeEndObject();

    //    generator.writeFieldName( "id" );
    //    generator.writeString( fileType.getId() );

    generator.writeEndObject();

    generator.close();
    assertEquals( BSON, Hex.encodeHexString( out.toByteArray() ) );
  }

  @Test
  public void testParse() throws Exception {
    JsonParser parser = jsonFactory.createJsonParser( Hex.decodeHex( BSON.toCharArray() ) );

    assertEquals( JsonToken.START_OBJECT, parser.nextToken() );

    assertEquals( JsonToken.FIELD_NAME, parser.nextToken() );
    assertEquals( "id", parser.getCurrentName() );
    assertEquals( JsonToken.VALUE_STRING, parser.nextToken() );
    assertEquals( "Canon Raw", parser.getText() );

    assertEquals( JsonToken.FIELD_NAME, parser.nextToken() );
    assertEquals( "dependent", parser.getCurrentName() );
    assertEquals( JsonToken.VALUE_FALSE, parser.nextToken() );
    assertFalse( parser.getBooleanValue() );

    assertEquals( JsonToken.FIELD_NAME, parser.nextToken() );
    assertEquals( "extension", parser.getCurrentName() );
    assertEquals( JsonToken.START_OBJECT, parser.nextToken() );

    assertEquals( JsonToken.FIELD_NAME, parser.nextToken() );
    assertEquals( "extension", parser.getCurrentName() );
    assertEquals( JsonToken.VALUE_STRING, parser.nextToken() );
    assertEquals( "cr2", parser.getText() );

    assertEquals( JsonToken.FIELD_NAME, parser.nextToken() );
    assertEquals( "default", parser.getCurrentName() );
    assertEquals( JsonToken.VALUE_TRUE, parser.nextToken() );
    assertTrue( parser.getBooleanValue() );

    assertEquals( JsonToken.FIELD_NAME, parser.nextToken() );
    assertEquals( "delimiter", parser.getCurrentName() );
    assertEquals( JsonToken.VALUE_STRING, parser.nextToken() );
    assertEquals( ".", parser.getText() );

    assertEquals( JsonToken.END_OBJECT, parser.nextToken() );
    assertEquals( JsonToken.END_OBJECT, parser.nextToken() );
    assertNull( parser.nextToken() );
  }
}
