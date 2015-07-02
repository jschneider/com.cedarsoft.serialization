package com.cedarsoft.serialization.serializers.jackson;

import java.io.IOException;
import java.util.BitSet;

import javax.annotation.Nonnull;

import com.cedarsoft.serialization.SerializationException;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class BitSetSerializer extends AbstractJacksonSerializer<BitSet> {
  public BitSetSerializer() {
    super("bitSet", VersionRange.single(1, 0, 0));
  }

  @Override
  public void serialize(@Nonnull JsonGenerator serializeTo, @Nonnull BitSet bitSet, @Nonnull Version formatVersion) throws IOException, VersionException, SerializationException, JsonProcessingException {
    serializeTo.writeStartArray();
    for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
      serializeTo.writeNumber(i);
    }
    serializeTo.writeEndArray();
  }

  @Nonnull
  @Override
  public BitSet deserialize(@Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion) throws IOException, VersionException, SerializationException, JsonProcessingException {
    BitSet bitSet = new BitSet();

    JacksonParserWrapper parserWrapper = new JacksonParserWrapper(deserializeFrom);
    while (parserWrapper.nextToken() != JsonToken.END_ARRAY) {
      Number value = parserWrapper.getNumberValue();
      bitSet.set(value.intValue());
    }

    return bitSet;
  }

  @Override
  public boolean isObjectType() {
    return false;
  }
}
