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
   * @param parser    the parser (currently at JsonToken#FIELD_NAME)
   * @param fieldName the field name
   * @throws java.io.IOException if there is an io problem
   */
  void skippingField( @Nonnull JsonParser parser, String fieldName ) throws IOException;

  /**
   * Is called when a field *value* is going to be skipped
   *
   * @param parser    the parser (currently at JsonToken#VALUE_*)
   * @param fieldName the field name
   * @throws java.io.IOException if there is an io problem
   */
  void skippingFieldValue( @Nonnull JsonParser parser, String fieldName ) throws IOException;
}
