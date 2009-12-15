package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractXmlVersionTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Money;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 *
 */
public class MoneySerializer2VersionsTest extends AbstractXmlVersionTest<Money> {
  @NotNull
  @Override
  protected Serializer<Money> getSerializer() {
    return new MoneySerializer2();
  }

  @NotNull
  @Override
  protected Map<? extends Version, ? extends String> getSerializedXml() {
    Map<Version, String> map = new HashMap<Version, String>();

    //We don't have to add the processing instruction containing the version. This is done
    //automatically
    map.put( new Version( 1, 0, 1 ), "<money cents=\"799\" />" );
    map.put( new Version( 1, 0, 0 ), "<money>799</money>" );

    return map;
  }

  @Override
  protected void verifyDeserialized( @NotNull Money deserialized, @NotNull Version version ) {
    assertEquals( new Money( 7, 99 ), deserialized );
  }
}
