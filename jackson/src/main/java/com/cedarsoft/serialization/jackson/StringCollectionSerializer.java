package com.cedarsoft.serialization.jackson;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class StringCollectionSerializer extends AbstractJacksonSerializer<Collection<? extends String>> {

  public StringCollectionSerializer() {
    super( "http://sun.com/java.lang.string", VersionRange.single( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Collection<? extends String> object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeStartArray();

    for ( String current : object ) {
      serializeTo.writeString( current );
    }

    serializeTo.writeEndArray();
  }

  @NotNull
  @Override
  public Collection<? extends String> deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    Collection<String> deserialized = new ArrayList<String>();
    while ( deserializeFrom.nextToken() != JsonToken.END_ARRAY ) {
      deserialized.add( deserializeFrom.getText() );
    }

    return deserialized;
  }

  @Override
  public boolean isObjectType() {
    return false;
  }
}
