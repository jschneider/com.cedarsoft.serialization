package com.cedarsoft.serialization.serializers.jackson;

import java.util.BitSet;

import javax.annotation.Nonnull;

import org.junit.experimental.theories.*;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BitSetSerializerTest extends AbstractJsonSerializerTest2<BitSet> {

  @DataPoint
  public static Entry<? extends BitSet> entry1() {
    BitSet bitSet = new BitSet(50);
    bitSet.set(0);
    bitSet.set(2);
    bitSet.set(3);
    bitSet.set(100);

    return BitSetSerializerTest.create(
      bitSet, BitSetSerializerTest.class.getResource("BitSet_1.0.0_1.json"));
  }

  @Nonnull
  @Override
  protected StreamSerializer<BitSet> getSerializer() throws Exception {
    return new BitSetSerializer();
  }
}