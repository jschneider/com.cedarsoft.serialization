package com.cedarsoft.serialization.jackson;

import com.cedarsoft.JsonUtils;
import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

import java.io.ByteArrayOutputStream;

/**
 *
 */
public class JacksonStringSerializerTest extends AbstractJsonSerializerTest2<String> {
  @Override
  protected boolean addNameSpace() {
    return false;
  }

  @NotNull
  @Override
  protected JacksonStringSerializer getSerializer() throws Exception {
    return new JacksonStringSerializer();
  }

  @Test
  public void testIt() throws Exception {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );

    getSerializer().serialize( generator, "asdf", Version.valueOf( 1, 0, 0 ) );

    generator.close();
    JsonUtils.assertJsonEquals( "\"asdf\"", out.toString() );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( "asdf", "\"asdf\"" );
}
