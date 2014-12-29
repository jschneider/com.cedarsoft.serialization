package com.cedarsoft.serialization.jackson;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class CollectionSerializer<T> extends AbstractJacksonSerializer<List<? extends T>> {
  @Nonnull
  private final Class<T> listType;

  public CollectionSerializer(@Nonnull Class<T> listType, @Nonnull AbstractJacksonSerializer<T> serializer) {
    this(listType, serializer, serializer.getType() + "s", serializer.getFormatVersionRange());
  }

  public CollectionSerializer(@Nonnull Class<T> listType, @Nonnull AbstractJacksonSerializer<T> serializer, @Nonnull String type, @Nonnull VersionRange formatVersionRange) {
    super(type, formatVersionRange);
    this.listType = listType;

    add(serializer).responsibleFor(listType).map(formatVersionRange).toDelegateVersion(serializer.getFormatVersion());
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize(@Nonnull JsonGenerator serializeTo, @Nonnull List<? extends T> object, @Nonnull Version formatVersion) throws IOException, VersionException, JsonProcessingException {
    verifyVersionWritable(formatVersion);
    serializeArray(object, listType, getType(), serializeTo, formatVersion);
  }

  @Nonnull
  @Override
  public List<? extends T> deserialize(@Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion) throws IOException, VersionException, JsonProcessingException {
    verifyVersionReadable(formatVersion);

    List<? extends T> elements = deserializeArray(listType, getType(), deserializeFrom, formatVersion);

    JacksonParserWrapper parser = new JacksonParserWrapper(deserializeFrom);
    parser.closeObject();

    return elements;
  }
}
