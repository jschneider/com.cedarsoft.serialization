package com.cedarsoft.serialization.jackson;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FloatSerializer extends AbstractJacksonSerializer<Float> {
  @Inject
  public FloatSerializer() {
    super( "float", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Float object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeNumber( object );
  }

  @Nonnull
  @Override
  public Float deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    return deserializeFrom.getFloatValue();
  }

  @Override
  public boolean isObjectType() {
    return false;
  }
}
