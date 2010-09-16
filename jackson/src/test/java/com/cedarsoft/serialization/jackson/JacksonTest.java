package com.cedarsoft.serialization.jackson;

import com.cedarsoft.JsonUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );
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

  private void verifyGenerator( @NotNull @NonNls String control ) throws IOException {
    generator.flush();
    JsonUtils.assertJsonEquals( control, out.toString() );
  }
}
