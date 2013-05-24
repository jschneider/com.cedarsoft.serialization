package com.cedarsoft.serialization.jackson.filter;

import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.test.utils.MockitoTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FilteringJsonParserTest {
  @Test
  public void testListener() throws Exception {
    final Filter filter = new Filter() {
      @Override
      public boolean shallFilterOut( @Nonnull JsonParser parser ) throws IOException, JsonParseException {
        return parser.getCurrentName().startsWith( "_" );
      }
    };

    new MockitoTemplate() {
      @Mock
      private FilteredParserListener listener;

      private final InputStream in = getClass().getResourceAsStream( "filter.json" );
      private final FilteringJsonParser parser = new FilteringJsonParser( new JsonFactory().createParser( in ), filter );
      private final JacksonParserWrapper wrapper = new JacksonParserWrapper( parser );

      @Override
      protected void stub() throws Exception {
      }

      @Override
      protected void execute() throws Exception {
        parser.addListener( listener );

        try {
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

      @Override
      protected void verifyMocks() throws Exception {
        Mockito.verify( listener ).skippingField( parser, "_filter1" );
        Mockito.verify( listener ).skippingFieldValue( parser, "_filter1" );
        Mockito.verify( listener ).skippingField( parser, "_filter2" );
        Mockito.verify( listener ).skippingFieldValue( parser, "_filter2" );
        Mockito.verify( listener ).skippingField( parser, "_filter3" );
        Mockito.verify( listener ).skippingFieldValue( parser, "_filter3" );
        Mockito.verify( listener ).skippingField( parser, "_filter4" );
        Mockito.verify( listener ).skippingFieldValue( parser, "_filter4" );
        Mockito.verify( listener ).skippingField( parser, "_filter5" );
        Mockito.verify( listener ).skippingFieldValue( parser, "_filter5" );
        Mockito.verify( listener ).skippingField( parser, "_filter6" );
        Mockito.verify( listener ).skippingFieldValue( parser, "_filter6" );

        Mockito.verifyNoMoreInteractions( listener );
      }
    }.run();
  }

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
