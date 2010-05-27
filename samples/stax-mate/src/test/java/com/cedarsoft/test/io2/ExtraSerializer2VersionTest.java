package com.cedarsoft.test.io2;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractSerializer;
import com.cedarsoft.serialization.AbstractXmlVersionTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.ui.DelegatesMappingVisualizer;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Money;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Testing the new version.
 */
public class ExtraSerializer2VersionTest extends AbstractXmlVersionTest<Extra> {
  @NotNull
  @Override
  protected Serializer<Extra> getSerializer() throws Exception {
    return new ExtraSerializer2( new MoneySerializer2() );
  }

  @NotNull
  @Override
  protected Map<? extends Version, ? extends String> getSerializedXml() {
    Map<Version, String> map = new HashMap<Version, String>();

    map.put( Version.valueOf( 1, 5, 0 ), "<extra>\n" +
      "  <description>Metallic</description>\n" +
      "  <price>40001</price>\n" +
      "</extra>" );

    map.put( Version.valueOf( 1, 5, 1 ), "<extra>\n" +
      "  <description>Metallic</description>\n" +
      "  <price cents=\"40001\"/>\n" +
      "</extra>" );

    return map;
  }

  @Override
  protected void verifyDeserialized( @NotNull Extra deserialized, @NotNull Version version ) throws Exception {
    assertEquals( deserialized.getDescription(), "Metallic" );
    assertEquals( deserialized.getPrice(), new Money( 400, 01 ) );
  }

  @Test
  public void testAsciiArt() throws Exception {
    DelegatesMappingVisualizer visualizer = new DelegatesMappingVisualizer( ( ( AbstractSerializer<?, ?, ?, ?> ) getSerializer() ).getDelegatesMappings() );
    assertEquals( visualizer.visualize(),
                  "         -->     Money\n" +
                    "----------------------\n" +
                    "   1.5.0 -->     1.0.0\n" +
                    "   1.5.1 -->     1.0.1\n" +
                    "----------------------\n" );
  }
}
