package com.cedarsoft.serialization.jackson.filter;

import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FilteringJsonParserTest {
  @Test
  public void testIt() throws Exception {
    JsonFactory jsonFactory = new JsonFactory();
    InputStream in = getClass().getResourceAsStream( "filter.json" );
    try {
      FilteringJsonParser parser = new FilteringJsonParser( jsonFactory.createParser( in ), new Filter() {
        @Override
        public boolean shallFilterOut( @Nonnull JsonParser parser ) throws IOException, JsonParseException {
          return parser.getCurrentName().startsWith( "_" );
        }
      } );

      JacksonParserWrapper wrapper = new JacksonParserWrapper( parser );

      wrapper.nextToken( JsonToken.START_OBJECT );
      wrapper.nextField( "id" );
      wrapper.nextToken( JsonToken.VALUE_STRING );

      wrapper.nextField( "key" );
      wrapper.nextToken( JsonToken.START_OBJECT );
      wrapper.nextField( "description" );
      wrapper.nextToken( JsonToken.VALUE_STRING );
      wrapper.nextToken( JsonToken.END_OBJECT );

      wrapper.nextField( "value" );
      wrapper.nextToken( JsonToken.START_OBJECT );
      wrapper.nextField( "@version" );
      wrapper.nextToken( JsonToken.VALUE_STRING );
      wrapper.nextField( "aValue" );
      wrapper.nextToken( JsonToken.VALUE_NUMBER_INT );
      wrapper.nextToken( JsonToken.END_OBJECT );

      wrapper.nextToken( JsonToken.END_OBJECT );
      wrapper.ensureObjectClosed();
    } finally {
      in.close();
    }
  }
}
