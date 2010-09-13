package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.file.BaseName;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

public class BaseNameSerializerVersionTest
  extends AbstractJsonVersionTest2<BaseName> {

  @DataPoint
  public static final VersionEntry ENTRY1 = BaseNameSerializerVersionTest.create(
    Version.valueOf( 1, 0, 0 ),
    BaseNameSerializerVersionTest.class.getResource( "BaseName_1.0.0_1.json" ) );

  @Override
  protected Serializer<BaseName> getSerializer()
    throws Exception {
    return new BaseNameSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull BaseName deserialized, @NotNull Version version )
    throws Exception {
    Assert.assertEquals( "daName", deserialized.getName() );
  }

}
