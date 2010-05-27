package com.cedarsoft.test.io2;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ExtraSerializerTest extends AbstractXmlSerializerTest<Extra> {
  @NotNull
  @Override
  protected Serializer<Extra> getSerializer() throws Exception {
    return new ExtraSerializer2( new MoneySerializer2() );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<extra>\n" +
      "  <description>descr</description>\n" +
      "  <price cents=\"7099\" />\n" +
      "</extra>";
  }

  @NotNull
  @Override
  protected Extra createObjectToSerialize() throws Exception {
    return new Extra( "descr", new Money( 70, 99 ) );
  }
}
