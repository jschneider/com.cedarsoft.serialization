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
import java.util.Arrays;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

/**
 *
 */
public class StringCollectionSerializerTest extends AbstractJsonSerializerTest2<Collection<? extends String>> {
  @Override
  protected boolean addNameSpace() {
    return false;
  }

  @NotNull
  @Override
  protected StringCollectionSerializer getSerializer() throws Exception {
    return new StringCollectionSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull Collection<? extends String> deserialized, @NotNull Collection<? extends String> original ) {
    assertThat( deserialized ).isEqualTo( original );
  }

  @Test
  public void testIt() throws Exception {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );

    getSerializer().serialize( generator, Arrays.asList( "a", "b", "c" ), Version.valueOf( 1, 0, 0 ) );

    generator.close();
    JsonUtils.assertJsonEquals( "[ \"a\", \"b\", \"c\" ]", out.toString() );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( Arrays.asList( "a", "b", "c" ), "[ \"a\", \"b\", \"c\" ]" );
}
