package com.cedarsoft.serialization.stax;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Support class for stax mate
 */
public class StaxMateSupport {
  @NotNull
  static final SMInputFactory SM_INPUT_FACTORY = new SMInputFactory( StaxSupport.XML_INPUT_FACTORY );
  @NotNull
  static final SMOutputFactory SM_OUTPUT_FACTORY = new SMOutputFactory( StaxSupport.XML_OUTPUT_FACTORY );

  private StaxMateSupport() {
  }

  /**
   * Returns the cached sm output factory
   *
   * @return the cached sm output factory
   */
  @NotNull
  public static SMOutputFactory getSmOutputFactory() {
    return SM_OUTPUT_FACTORY;
  }

  /**
   * Returns the cached sm input factory
   *
   * @return the cached sm input factory
   */
  @NotNull
  public static SMInputFactory getSmInputFactory() {
    return SM_INPUT_FACTORY;
  }
}
