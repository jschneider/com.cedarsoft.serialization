package com.cedarsoft.serialization.jackson.test.compatible;

import org.codehaus.jackson.JsonParser;

import javax.annotation.Nonnull;

/**
 * @deprecated use the class {@link com.cedarsoft.serialization.jackson.JacksonParserWrapper} instead.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
@Deprecated
public class JacksonParserWrapper extends com.cedarsoft.serialization.jackson.JacksonParserWrapper{
  public JacksonParserWrapper( @Nonnull JsonParser parser ) {
    super( parser );
  }
}
