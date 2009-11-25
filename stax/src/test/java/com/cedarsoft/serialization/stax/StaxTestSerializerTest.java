package com.cedarsoft.serialization.stax;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import javax.xml.stream.XMLInputFactory;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxTestSerializerTest extends AbstractXmlSerializerTest<Integer> {
  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<int>7</int>";
  }

  @NotNull
  @Override
  protected Serializer<Integer> getSerializer() {
    return new StaxIntegerSerializer();
  }

  @NotNull
  @Override
  protected Integer createObjectToSerialize() {
    return 7;
  }

  @Test
  public void testIt() {
    assertEquals( XMLInputFactory.newInstance( "com.sun.xml.internal.stream.XMLInputFactoryImpl", getClass().getClassLoader() ).getClass().getName(), "com.sun.xml.internal.stream.XMLInputFactoryImpl" );
    assertEquals( XMLInputFactory.newInstance( "com.sun.xml.internal.stream.XMLInputFactoryImpl", getClass().getClassLoader() ).getClass().getName(), "com.sun.xml.internal.stream.XMLInputFactoryImpl" );
  }
}
