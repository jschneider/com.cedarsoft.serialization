package com.cedarsoft.serialization.jackson;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 */
public class JacksonStringSerializer extends AbstractJacksonSerializer<String> {
  public JacksonStringSerializer() {
    super( "http://sun.com/java.lang.string", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull String object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeString( object );
  }

  @NotNull
  @Override
  public String deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    return deserializeFrom.getText();
  }

  @Override
  public boolean isObjectType() {
    return false;
  }
}
