package com.cedarsoft.test.io2;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Model;
import com.cedarsoft.test.Money;
import com.cedarsoft.test.io.ModelSerializer;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Arrays;

import static org.testng.Assert.*;

/**
 *
 */
public class CarSerializer2Test extends AbstractXmlSerializerTest<Car> {
  @NotNull
  @Override
  protected Serializer<Car> getSerializer() throws Exception {
    MoneySerializer2 moneySerializer = new MoneySerializer2();
    return new CarSerializer2( moneySerializer, new ExtraSerializer2( moneySerializer ), new ModelSerializer() );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return
      "<car>\n" +
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
        "</car>";
  }

  @NotNull
  @Override
  protected Car createObjectToSerialize() throws Exception {
    return new Car( new Model( "Ford" ), Color.ORANGE, new Money( 19000, 00 ), Arrays.asList( new Extra( "Whoo effect", new Money( 99, 98 ) ), new Extra( "Better Whoo effect", new Money( 199, 00 ) ) ) );
  }

  @Override
  protected void verifyDeserialized( @NotNull Car deserialized ) throws Exception {
    assertEquals( deserialized.getColor(), Color.ORANGE );
    assertEquals( deserialized.getBasePrice(), new Money( 19000, 0 ) );
    //.... (and much more)
  }
}
