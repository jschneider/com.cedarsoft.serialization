package com.cedarsoft.serialization.json;

import com.cedarsoft.file.BaseName;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.AbstractSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

public class BaseNameSerializerTest extends AbstractJsonSerializerTest2<BaseName> {
  @DataPoint
  public static final Entry<? extends BaseName> ENTRY1 = AbstractSerializerTest2.create(
    new BaseName( "daName" ),
    BaseNameSerializerTest.class.getResource( "BaseName_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<BaseName> getSerializer() throws Exception {
    return new BaseNameSerializer();
  }

}
