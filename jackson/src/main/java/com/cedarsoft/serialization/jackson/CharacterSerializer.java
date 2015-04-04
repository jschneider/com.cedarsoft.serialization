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
public class CharacterSerializer extends AbstractJacksonSerializer<Character> {
  @Inject
  public CharacterSerializer() {
    super( "char", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Character object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeString( Character.toString( object ) );
  }

  @Nonnull
  @Override
  public Character deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    String trimmed = deserializeFrom.getText().trim();
    if ( trimmed.length() != 1 ) {
      throw new IllegalStateException( "Cannot convert <" + trimmed + "> to char" );
    }
    return trimmed.charAt( 0 );
  }

  @Override
  public boolean isObjectType() {
    return false;
  }
}
