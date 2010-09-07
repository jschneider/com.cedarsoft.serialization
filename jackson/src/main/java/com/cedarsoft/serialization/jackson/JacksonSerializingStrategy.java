package com.cedarsoft.serialization.jackson;

import com.cedarsoft.serialization.SerializingStrategy;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface JacksonSerializingStrategy<T> extends SerializingStrategy<T, JsonGenerator, JsonParser, JsonProcessingException> {
}
