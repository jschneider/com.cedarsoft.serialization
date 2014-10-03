package com.cedarsoft.serialization.jackson.filter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface Filter {
  /**
   * <p>
   * Returns whether the current token of the given parser shall be filtered out.<br>
   * {@code parser.getCurrentToken()} always returns JsonToken#FIELD_NAME.
   * </p>
   *
   * <p>
   * ATTENTION: Do *not* call nextToken() on the given parser.
   * </p>
   *
   * @param parser the parser
   * @return true if the current token (and its children) should be filtered out, false otherwise
   * @throws IOException if there is an io problem
   */
  boolean shallFilterOut( @Nonnull JsonParser parser ) throws IOException, JsonParseException;

}