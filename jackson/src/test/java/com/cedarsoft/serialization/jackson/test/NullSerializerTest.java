package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.SerializationException;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.DoubleSerializer;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.serialization.jackson.StringSerializer;
import com.cedarsoft.test.utils.JsonUtils;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.google.inject.Guice;
import org.junit.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class NullSerializerTest {
  @Test
  public void basics() throws Exception {
    MySerializer serializer = Guice.createInjector().getInstance(MySerializer.class);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize(new MyObject(null, null), out);

    JsonUtils.assertJsonEquals("{\n" +
                                 "  \"@type\" : \"myobject\",\n" +
                                 "  \"@version\" : \"1.0.0\",\n" +
                                 "  \"value\" : null\n" +
                                 "}", out.toString());


    MyObject deserialized = serializer.deserialize(new ByteArrayInputStream(out.toByteArray()));

    assertThat(deserialized.getNumber()).isNull();
    assertThat(deserialized.getValue()).isNull();
  }

  public static final class MySerializer extends AbstractJacksonSerializer<MyObject> {

    public static final String VALUE = "value";
    public static final String NUMBER = "number";

    @Inject
    public MySerializer(@Nonnull DoubleSerializer doubleSerializer, @Nonnull StringSerializer stringSerializer) {
      super("myobject", VersionRange.single(1, 0, 0));

      getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
      getDelegatesMappings().add(doubleSerializer).responsibleFor(Double.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
      assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@Nonnull JsonGenerator serializeTo, @Nonnull MyObject object, @Nonnull Version formatVersion) throws IOException, VersionException, SerializationException, JsonProcessingException {
      serializeIfNotNull(object.getNumber(), Double.class, NUMBER, serializeTo, formatVersion);
      serialize(object.getValue(), String.class, VALUE, serializeTo, formatVersion);
    }

    @Nonnull
    @Override
    public MyObject deserialize(@Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion) throws IOException, VersionException, SerializationException, JsonProcessingException {
      verifyVersionReadable(formatVersion);

      @Nullable Double number = null;
      @Nullable String value = null;

      JacksonParserWrapper parser = new JacksonParserWrapper(deserializeFrom);
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String currentName = parser.getCurrentName();

        if (currentName.equals(NUMBER)) {
          parser.nextToken();
          number = deserializeNullable(Double.class, formatVersion, deserializeFrom);
          continue;
        }
        if (currentName.equals(VALUE)) {
          parser.nextToken();
          value = deserializeNullable(String.class, formatVersion, deserializeFrom);
          continue;
        }
        throw new IllegalStateException("Unexpected field reached <" + currentName + ">");
      }

      parser.ensureObjectClosed();

      return new MyObject(value, number);
    }
  }

  public static class MyObject {
    @Nullable
    private final String value;
    @Nullable
    private final Double number;

    public MyObject(String value, Double number) {
      this.value = value;
      this.number = number;
    }

    @Nullable
    public String getValue() {
      return value;
    }

    @Nullable
    public Double getNumber() {
      return number;
    }
  }

}
