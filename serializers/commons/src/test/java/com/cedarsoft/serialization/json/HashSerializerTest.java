package com.cedarsoft.serialization.json;

import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

public class HashSerializerTest
  extends AbstractJsonSerializerTest2<Hash> {

  @DataPoint
  public static final Entry<? extends Hash> ENTRY1 = HashSerializerTest.create(
    new Hash( Algorithm.MD5, "HASH".getBytes() ),
    HashSerializerTest.class.getResource( "Hash_1.0.0_1.json" )
  );

  @NotNull
  @Override
  protected Serializer<Hash> getSerializer() throws Exception {
    return new HashSerializer();
  }

}
