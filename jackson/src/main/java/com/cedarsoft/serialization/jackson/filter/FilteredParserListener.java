package com.cedarsoft.serialization.jackson.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface FilteredParserListener {
  /**
   * Is called when a field is going to be skipped
   *
   * @param parser    the parser (currently at {@link JsonToken#FIELD_NAME}
   * @param fieldName the field name
   */
  void skippingField( @Nonnull JsonParser parser, String fieldName ) throws IOException;

  /**
   * Is called when a field *value* is going to be skipped
   *
   * @param parser    the parser (currently at JsonToken#VALUE_*)
   * @param fieldName the field name
   */
  void skippingFieldValue( @Nonnull JsonParser parser, String fieldName ) throws IOException;
}
