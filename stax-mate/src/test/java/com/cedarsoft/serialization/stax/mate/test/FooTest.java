package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FooTest extends AbstractXmlSerializerTest2<Foo> {
  @Nonnull
  @Override
  protected Foo.Serializer getSerializer() throws Exception {
    return new Foo.Serializer();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create(
    new Foo( "hello", Direction.NORTH ), "<foo description=\"hello\" direction=\"NORTH\" />" );

  @DataPoint
  public static final Entry<?> ENTRY2 = create(
    new Foo( "asdf", Direction.SOUTH ), "<foo description=\"asdf\" direction=\"SOUTH\" />" );

}
