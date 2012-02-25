package com.cedarsoft.serialization.jackson.test.compatible;

import com.cedarsoft.serialization.jackson.JacksonSupport;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

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
    generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );
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
