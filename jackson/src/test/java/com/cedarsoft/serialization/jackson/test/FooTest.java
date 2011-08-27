package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FooTest extends AbstractJsonSerializerTest2<Foo> {
  @Nonnull
  @Override
  protected Foo.Serializer getSerializer() throws Exception {
    return new Foo.Serializer();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create(
    new Foo( "hello", Direction.NORTH ),
    "{\n" +
      "  \"description\" : \"hello\",\n" +
      "  \"direction\" : \"NORTH\"\n" +
      "}" );

  @DataPoint
  public static final Entry<?> ENTRY2 = create(
    new Foo( "asdf", Direction.SOUTH ),
    "{\n" +
      "  \"description\" : \"asdf\",\n" +
      "  \"direction\" : \"SOUTH\"\n" +
      "}" );

}
