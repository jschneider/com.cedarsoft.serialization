package com.cedarsoft.serialization.serializers.json;

import java.io.IOException;
import java.net.URL;

import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;

import javax.annotation.Nonnull;

public class UrlSerializer extends AbstractJacksonSerializer<URL> {
  public UrlSerializer() {
    super( "url", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull URL object, @Nonnull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionWritable( formatVersion );
    serializeTo.writeString( object.toString() );
  }

  @Nonnull
  @Override
  public URL deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );

    final String text = parser.getText();
    return new URL( text );
  }

  @Override
  public boolean isObjectType() {
    return false;
  }
}
