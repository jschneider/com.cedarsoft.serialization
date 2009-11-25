package com.cedarsoft.serialization;

import com.cedarsoft.serialization.stax.AbstractStaxMateSerializerTest;
import org.jetbrains.annotations.NotNull;

import java.awt.Dimension;

import static org.testng.Assert.*;

/**
 *
 */
public class DimensionSerializerTest extends AbstractStaxMateSerializerTest<Dimension> {
  @NotNull
  @Override
  protected DimensionSerializer getSerializer() {
    return new DimensionSerializer();
  }

  @NotNull
  @Override
  protected Dimension createObjectToSerialize() {
    return new Dimension( 1600, 600 );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<dimension>1600x600</dimension>";
  }

  @Override
  protected void verifyDeserialized( @NotNull Dimension deserialized ) {
    assertEquals( deserialized.width, 1600 );
    assertEquals( deserialized.height, 600 );
  }
}
