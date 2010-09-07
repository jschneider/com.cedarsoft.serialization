package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.AbstractSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class EmailSerializerTest extends AbstractSerializerTest2<Email> {
  @Override
  protected void verifySerialized( @NotNull Entry<Email> entry, @NotNull byte[] serialized ) throws Exception {
    assertEquals( new String( entry.getExpected() ), new String( serialized ) );
  }

  @NotNull
  @Override
  protected Serializer<Email> getSerializer() throws Exception {
    return new EmailSerializer();
  }

  @DataPoint
  public static Entry<?> json() {
    return create( new Email( "test@test.de" ), "{\"$\":\"test@test.de\"}".getBytes() );
  }
}
