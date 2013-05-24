package com.cedarsoft.serialization.jackson.filter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserDelegate;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This is a special class that filters out specific fields
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FilteringJsonParser extends JsonParserDelegate {
  @Nonnull
  private final Filter filter;

  public FilteringJsonParser( @Nonnull JsonParser parser, @Nonnull Filter filter ) {
    super( parser );
    this.filter = filter;
  }

  @Override
  public JsonToken nextToken() throws IOException, JsonParseException {
    super.nextToken();

    //If it is a couch field, please skip it
    while ( getCurrentToken() == JsonToken.FIELD_NAME && filter.shallFilterOut( this ) ) {
      skipToNextField();
    }

    return getCurrentToken();
  }

  protected void skipToNextField() throws IOException {
    JsonToken token = super.nextToken();

    if ( token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT ) {
      skipChildren();
      super.nextToken();
    } else if ( isValue( token ) ) {
      super.nextToken();
    } else {
      throw new IllegalStateException( "??? " + getCurrentToken() );
    }
  }

  private static boolean isValue( @Nonnull JsonToken token ) {
    if ( token == JsonToken.VALUE_EMBEDDED_OBJECT ) {
      return true;
    }
    if ( token == JsonToken.VALUE_FALSE ) {
      return true;
    }
    if ( token == JsonToken.VALUE_NULL ) {
      return true;
    }
    if ( token == JsonToken.VALUE_NUMBER_FLOAT ) {
      return true;
    }
    if ( token == JsonToken.VALUE_NUMBER_INT ) {
      return true;
    }
    if ( token == JsonToken.VALUE_STRING ) {
      return true;
    }
    if ( token == JsonToken.VALUE_STRING ) {
      return true;
    }

    return false;
  }
}
