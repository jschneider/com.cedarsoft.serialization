package com.cedarsoft.serialization.stax;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * Helper class for stax
 */
public class StaxSupport {
  private StaxSupport() {
  }

  @NotNull
  static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
  @NotNull
  static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

  @NotNull
  public static XMLOutputFactory getXmlOutputFactory() {
    return XML_OUTPUT_FACTORY;
  }

  @NotNull
  public static XMLInputFactory getXmlInputFactory() {
    return XML_INPUT_FACTORY;
  }
}
