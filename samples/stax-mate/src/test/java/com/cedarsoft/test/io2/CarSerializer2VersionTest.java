package com.cedarsoft.test.io2;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractSerializer;
import com.cedarsoft.serialization.AbstractXmlVersionTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.ui.DelegatesMappingVisualizer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Money;
import com.cedarsoft.test.io.ModelSerializer;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 *
 */
public class CarSerializer2VersionTest extends AbstractXmlVersionTest<Car> {
  @NotNull
  @Override
  protected Serializer<Car> getSerializer() throws Exception {
    MoneySerializer2 moneySerializer = new MoneySerializer2();
    return new CarSerializer2( moneySerializer, new ExtraSerializer2( moneySerializer ), new ModelSerializer() );
  }

  @NotNull
  @Override
  protected Map<? extends Version, ? extends String> getSerializedXml() {
    Map<Version, String> map = new HashMap<Version, String>();

    map.put( Version.valueOf( 1, 0, 0 ), "<car>\n" +
      "  <color red=\"255\" blue=\"0\" green=\"200\" />\n" +
      "  <model>Ford</model>\n" +
      "  <basePrice>1900000</basePrice>\n" +
      "  <extra>\n" +
      "    <description>Whoo effect</description>\n" +
      "    <price>9998</price>\n" +
      "  </extra>" +
      " <extra>\n" +
      "    <description>Better Whoo effect</description>\n" +
      "    <price>19900</price>\n" +
      "  </extra>" +
      "</car>" );
    map.put( Version.valueOf( 1, 0, 1 ), "<car>\n" +
      "  <color red=\"255\" blue=\"0\" green=\"200\" />\n" +
      "  <model>Ford</model>\n" +
      "  <basePrice cents=\"1900000\" />\n" +
      "  <extra>\n" +
      "    <description>Whoo effect</description>\n" +
      "    <price cents=\"9998\" />\n" +
      "  </extra>\n" +
      "  <extra>\n" +
      "    <description>Better Whoo effect</description>\n" +
      "    <price cents=\"19900\" />\n" +
      "  </extra>\n" +
      "</car>" );

    return map;
  }

  @Override
  protected void verifyDeserialized( @NotNull Car deserialized, @NotNull Version version ) throws Exception {
    assertEquals( deserialized.getColor(), Color.ORANGE );
    assertEquals( deserialized.getBasePrice(), new Money( 19000, 0 ) );
    //.... (and much more)
  }

  @Test
  public void testAsciiArt() throws Exception {
    DelegatesMappingVisualizer visualizer = new DelegatesMappingVisualizer( ( ( AbstractSerializer<?, ?, ?, ?> ) getSerializer() ).getDelegatesMappings() );
    assertEquals( visualizer.visualize(),
                  "         -->     Extra     Model     Money\n" +
                    "------------------------------------------\n" +
                    "   1.0.0 -->     1.5.0     1.0.0     1.0.0\n" +
                    "   1.0.1 -->     1.5.1       |       1.0.1\n" +
                    "------------------------------------------\n" );
  }

}
