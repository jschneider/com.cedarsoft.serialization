package com.cedarsoft.serialization.jackson;

import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractSerializer;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractJacksonSerializer<T> extends AbstractSerializer<T, JsonGenerator, JsonParser, JsonProcessingException> {
  @NonNls
  public static final String FIELD_NAME_DEFAULT_TEXT = "$";

  protected AbstractJacksonSerializer( @NotNull VersionRange formatVersionRange ) {
    super( formatVersionRange );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();

    JsonGenerator generator = jsonFactory.createJsonGenerator( out, JsonEncoding.UTF8 );

    generator.writeStartObject();
    //    String nameSpace = getNameSpaceUri();
    //    generator.writeDefaultNamespace( nameSpace );
    //Sets the name space

    serialize( generator, object, getFormatVersion() );
    generator.writeEndObject();

    generator.close();
  }

  @NotNull
  @Override
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    JsonFactory jsonFactory = JacksonSupport.getJsonFactory();

    JsonParser parser = jsonFactory.createJsonParser( in );

    //todo verify namespace

    nextToken( parser, JsonToken.START_OBJECT );

    T deserialized = deserialize( parser, getFormatVersion() );

    if ( parser.getCurrentToken() != JsonToken.END_OBJECT ) {
      throw new JsonParseException( "No consumed everything", parser.getCurrentLocation() );
    }

    if ( parser.nextToken() != null ) {
      throw new JsonParseException( "No consumed everything", parser.getCurrentLocation() );
    }

    parser.close();

    return deserialized;
  }

  protected void nextToken( @NotNull JsonParser parser, @NotNull JsonToken expected ) throws IOException {
    JsonToken current = parser.nextToken();
    if ( current != expected ) {
      throw new IllegalStateException( "Invalid token. Expected <" + expected + "> but got <" + current + ">" );
    }
  }
}
