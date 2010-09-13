package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

public class HashSerializerVersionTest
  extends AbstractJsonVersionTest2<Hash> {

  @DataPoint
  public static final VersionEntry ENTRY1 = HashSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), HashSerializerVersionTest.class.getResource( "Hash_1.0.0_1.json" ) );

  @NotNull
  @Override
  protected Serializer<Hash> getSerializer() throws Exception {
    return new HashSerializer();
  }

  @Override
  protected void verifyDeserialized( @NotNull Hash deserialized, @NotNull Version version )
    throws Exception {
    Assert.assertEquals( Algorithm.MD5, deserialized.getAlgorithm() );
    Assert.assertEquals( "HASH", new String( deserialized.getValue() ) );
  }

}
