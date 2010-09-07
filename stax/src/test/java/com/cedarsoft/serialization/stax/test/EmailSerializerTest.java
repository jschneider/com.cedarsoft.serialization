package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class EmailSerializerTest extends AbstractXmlSerializerTest2<Email> {
  @NotNull
  @Override
  protected Serializer<Email> getSerializer() throws Exception {
    return new EmailSerializer();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( new Email( "test@test.de" ), "<email>test@test.de</email>" );
}
