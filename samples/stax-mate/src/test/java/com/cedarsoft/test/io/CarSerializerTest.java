package com.cedarsoft.test.io;

import com.cedarsoft.serialization.AbstractXmlSerializerMultiTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Car;
import com.cedarsoft.test.Extra;
import com.cedarsoft.test.Model;
import com.cedarsoft.test.Money;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 *
 */
public class CarSerializerTest extends AbstractXmlSerializerMultiTest<Car> {
  @NotNull
  @Override
  protected Serializer<Car> getSerializer() {
    MoneySerializer moneySerializer = new MoneySerializer();
    //We can share the same serializer. But we don't have to.
    return new CarSerializer( moneySerializer, new ExtraSerializer( moneySerializer ), new ModelSerializer() );
  }

  @NotNull
  @Override
  protected Iterable<? extends Car> createObjectsToSerialize() {
    return Arrays.asList(
      new Car( new Model( "Toyota" ), Color.BLACK, new Money( 49000, 00 ) ),
      new Car( new Model( "Ford" ), Color.ORANGE, new Money( 19000, 00 ), Arrays.asList( new Extra( "Whoo effect", new Money( 99, 98 ) ), new Extra( "Better Whoo effect", new Money( 199, 00 ) ) ) )
    );
  }

  @NotNull
  @Override
  protected List<? extends String> getExpectedSerialized() {
    return Arrays.asList(
      "<car>\n" +
        "  <color red=\"0\" blue=\"0\" green=\"0\" />\n" +
        "  <model>Toyota</model>\n" +
        "  <basePrice cents=\"4900000\" />\n" +
        "</car>",
      "<car>\n" +
        "  <color red=\"255\" blue=\"0\" green=\"200\" />\n" +
        "  <model>Ford</model>\n" +
        "  <basePrice cents=\"1900000\" />\n" +
        "  <extra>\n" +
        "    <description>Whoo effect</description>\n" +
        "    <price cents=\"9998\" />\n" +
        "  </extra>" +
        " <extra>\n" +
        "    <description>Better Whoo effect</description>\n" +
        "    <price cents=\"19900\" />\n" +
        "  </extra>" +
        "</car>" );
  }

  @Override
  protected void verifyDeserialized( @NotNull List<? extends Car> deserialized ) {
    //We don't implement equals in the car, therefore compare manually
    //    super.verifyDeserialized( deserialized );

    assertEquals( deserialized.size(), 2 );

    Car first = deserialized.get( 0 );
    assertEquals( first.getColor(), Color.BLACK );
    assertEquals( first.getBasePrice(), new Money( 49000, 0 ) );

    //....

  }
}
