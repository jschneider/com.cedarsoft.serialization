package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.test.utils.JsonUtils;
import org.codehaus.jackson.JsonParseException;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class WithoutTypeTest {
  @Test
  public void testWithoutType() throws Exception {
    Foo.Serializer serializer = new Foo.Serializer();


    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Foo( "descri", Direction.NORTH ), out );

    JsonUtils.assertJsonEquals( "{\n" +
                                  "  \"@type\" : \"foo\",\n" +
                                  "  \"@version\" : \"1.0.0\",\n" +
                                  "  \"description\" : \"descri\",\n" +
                                  "  \"direction\" : \"NORTH\"\n" +
                                  "}", out.toString() );

    String withoutType = "{\n" +
      "  \"description\" : \"descri\",\n" +
      "  \"direction\" : \"NORTH\"\n" +
      "}";

    try {
      serializer.deserialize( new ByteArrayInputStream( withoutType.getBytes() ) );
      fail( "Where is the Exception" );
    } catch ( JsonParseException e ) {
      assertThat( e.getMessage() ).startsWith( "Invalid field. Expected <@type> but was <description>" );
    }
  }
}
