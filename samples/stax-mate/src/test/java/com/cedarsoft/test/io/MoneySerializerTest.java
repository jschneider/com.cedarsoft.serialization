package com.cedarsoft.test.io;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Money;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MoneySerializerTest extends AbstractXmlSerializerTest<Money>{
  //We don't need a multi test for such an easy class...
  @NotNull
  @Override
  protected Serializer<Money> getSerializer() {
    return new MoneySerializer();
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<money cents=\"1199\"/>";
  }

  @NotNull
  @Override
  protected Money createObjectToSerialize() {
    return new Money( 1199 );
  }
}
