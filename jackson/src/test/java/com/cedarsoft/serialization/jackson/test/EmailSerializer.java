package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class EmailSerializer extends AbstractJacksonSerializer<Email> {
  public EmailSerializer() {
    super( "http://cedarsoft.com/test/email", VersionRange.from( 1, 0, 0 ).to() );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Email object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeStringField( FIELD_NAME_DEFAULT_TEXT, object.getMail() );
  }

  @NotNull
  @Override
  public Email deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    nextField( deserializeFrom, FIELD_NAME_DEFAULT_TEXT );
    try {
      return new Email( deserializeFrom.getText() );
    } finally {
      nextToken( deserializeFrom, JsonToken.END_OBJECT );
    }
  }
}
