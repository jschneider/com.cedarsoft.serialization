package com.cedarsoft.serialization.stax;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class StaxMateSupport {
  @NotNull
  static final SMInputFactory SM_INPUT_FACTORY = new SMInputFactory( StaxSupport.XML_INPUT_FACTORY );
  @NotNull
  static final SMOutputFactory SM_OUTPUT_FACTORY = new SMOutputFactory( StaxSupport.XML_OUTPUT_FACTORY );

  @NotNull
  public static SMOutputFactory getSmOutputFactory() {
    return SM_OUTPUT_FACTORY;
  }

  public static SMInputFactory getSmInputFactory() {
    return SM_INPUT_FACTORY;
  }
}
