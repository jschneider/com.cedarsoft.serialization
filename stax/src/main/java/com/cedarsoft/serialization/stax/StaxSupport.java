package com.cedarsoft.serialization.stax;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Field;

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

  /**
   * Returns the name for an event id
   *
   * @param eventId the event id
   * @return the name
   */
  @NotNull
  @NonNls
  public static String getEventName( int eventId ) {
    try {
      for ( Field field : XMLStreamReader.class.getFields() ) {
        if ( field.getType() != Integer.TYPE ) {
          continue;
        }

        Integer value = ( Integer ) field.get( null );
        if ( eventId == value ) {
          return field.getName();
        }
      }
      return String.valueOf( eventId );
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
      return "Unknown error: " + eventId;
    }

  }
}
