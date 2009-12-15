package com.cedarsoft.test.io;

import com.cedarsoft.serialization.AbstractXmlSerializerMultiTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ExtraSerializerTest extends AbstractXmlSerializerMultiTest<Extra> {
  @NotNull
  @Override
  protected Serializer<Extra> getSerializer() {
    //This serializer uses a delegate
    return new ExtraSerializer( new MoneySerializer() );
  }

  @NotNull
  @Override
  protected Iterable<? extends Extra> createObjectsToSerialize() {
    return Arrays.asList(
      new Extra( "Metallic", new Money( 400, 00 ) ),
      new Extra( "Great Radio", new Money( 700, 00 ) )
    );
  }

  @NotNull
  @Override
  protected List<? extends String> getExpectedSerialized() {
    return Arrays.asList(
      "<extra>\n" +
        "  <description>Metallic</description>\n" +
        "  <price>40000</price>\n" +
        "</extra>",
      "<extra>\n" +
        "  <description>Great Radio</description>\n" +
        "  <price>70000</price>\n" +
        "</extra>" );
  }
}
