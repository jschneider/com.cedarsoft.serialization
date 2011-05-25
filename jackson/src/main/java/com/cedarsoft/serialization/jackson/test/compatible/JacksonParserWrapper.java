package com.cedarsoft.serialization.jackson.test.compatible;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonParserWrapper {
  @Nonnull
  private final JsonParser parser;

  public JacksonParserWrapper( @Nonnull JsonParser parser ) {
    this.parser = parser;
  }

  public void startObject() throws IOException, JsonParseException {
    nextToken( JsonToken.START_OBJECT );
  }

  public void endObject() throws IOException, JsonParseException {
    nextToken( JsonToken.END_OBJECT );
  }

  public void nextField( @Nonnull String fieldName ) throws IOException {
    nextToken( JsonToken.FIELD_NAME );
    String currentName = parser.getCurrentName();

    //noinspection CallToStringEquals
    if ( !fieldName.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + fieldName + "> but was <" + currentName + ">", parser.getCurrentLocation() );
    }
  }

  public void nextToken( @Nonnull JsonToken expected ) throws IOException {
    parser.nextToken();
    verifyCurrentToken( expected );
  }

  public void verifyCurrentToken( @Nonnull JsonToken expected ) throws JsonParseException {
    JsonToken current = parser.getCurrentToken();
    if ( current != expected ) {
      throw new JsonParseException( "Invalid token. Expected <" + expected + "> but got <" + current + ">", parser.getCurrentLocation() );
    }
  }

  @Nonnull
  public String getValue() throws IOException {
    parser.nextToken();
    return parser.getText();
  }

  public int getValueAsInt() throws IOException {
    parser.nextToken();
    return parser.getIntValue();
  }
}
