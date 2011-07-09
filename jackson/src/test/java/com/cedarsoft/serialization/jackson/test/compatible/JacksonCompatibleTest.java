package com.cedarsoft.serialization.jackson.test.compatible;

import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonSupport;
import com.cedarsoft.test.utils.JsonUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.junit.*;

import java.io.ByteArrayOutputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonCompatibleTest {
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
  public void testIt() throws Exception {
    generator.writeStartObject();

    generator.writeStringField( "street", "Schlossalle" );
    generator.writeNumberField( "number", 7 );

    generator.writeEndObject();

    generator.flush();
    generator.close();

    JsonUtils.assertJsonEquals(getClass().getResource("simple.json"), out.toString());
  }

  @Test
  public void testReadCompatible() throws Exception {
    JsonParser parser = jsonFactory.createJsonParser( getClass().getResource( "simple.json" ) );

    JacksonParserWrapper wrapper = new JacksonParserWrapper( parser );
    wrapper.startObject();
    wrapper.nextField( "street" );

    assertThat( parser.getText() ).isEqualTo( "street" );
    assertThat( parser.nextToken() ).isEqualTo( JsonToken.VALUE_STRING );
    assertThat( parser.getText() ).isEqualTo( "Schlossalle" );

    wrapper.nextField( "number" );

    assertThat( parser.nextToken() ).isEqualTo( JsonToken.VALUE_NUMBER_INT );
    assertThat( parser.getValueAsInt() ).isEqualTo( 7 );

    assertThat( parser.nextToken() ).isEqualTo( JsonToken.END_OBJECT );

    wrapper.ensureObjectClosed();
  }
}
