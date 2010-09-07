package com.cedarsoft.serialization.jackson;

import org.codehaus.jackson.JsonFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSupport {
  @NotNull
  private static final JsonFactory JSON_FACTORY = new JsonFactory();

  @NotNull
  public static JsonFactory getJsonFactory() {
    return JSON_FACTORY;
  }
}
