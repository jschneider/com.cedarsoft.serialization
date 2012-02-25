package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.jackson.SkipTypeInfoSerializer;
import com.cedarsoft.test.utils.JsonUtils;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SkipTypeInfoSerializerTest {
  @Test
  public void testIt() throws Exception {
    SkipTypeInfoSerializer<Foo> serializer = new SkipTypeInfoSerializer<Foo>( new Foo.Serializer() );


    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Foo( "asdf", Direction.NORTH ), out );

    JsonUtils.assertJsonEquals( "{\n" +
                                  "  \"description\" : \"asdf\",\n" +
                                  "  \"direction\" : \"NORTH\"\n" +
                                  "}", out.toString() );

    Foo deserialized = serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) );
    assertThat( deserialized.getDescription() ).isEqualTo( "asdf" );
    assertThat( deserialized.getDirection() ).isSameAs( Direction.NORTH );
  }
}
