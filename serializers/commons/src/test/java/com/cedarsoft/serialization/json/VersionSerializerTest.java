package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

public class VersionSerializerTest
  extends AbstractJsonSerializerTest2<Version> {

  @DataPoint
  public static final Entry<? extends Version> ENTRY1 = VersionSerializerTest.create(
    new Version( 42, 42, 42, "suffix" ),
    VersionSerializerTest.class.getResource( "Version_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<Version> getSerializer() throws Exception {
    return new VersionSerializer();
  }
}
