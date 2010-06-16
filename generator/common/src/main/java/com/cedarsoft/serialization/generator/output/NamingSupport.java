package com.cedarsoft.serialization.generator.output;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class NamingSupport {


  @NotNull
  @NonNls
  public String createXmlElementName( @NotNull String simpleClassName ) {
    return simpleClassName.toLowerCase();
  }
}
