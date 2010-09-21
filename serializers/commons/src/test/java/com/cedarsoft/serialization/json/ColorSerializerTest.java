package com.cedarsoft.serialization.json;

import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import java.awt.Color;

/**
 *
 */
public class ColorSerializerTest extends AbstractJsonSerializerTest2<Color> {
  @DataPoint
  public static final Entry<? extends Color> ENTRY1 = AbstractJsonSerializerTest2.create(
    new Color( 100, 42, 130 ),
    ColorSerializerTest.class.getResource( "Color_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<Color> getSerializer() throws Exception {
    return new ColorSerializer();
  }
}
