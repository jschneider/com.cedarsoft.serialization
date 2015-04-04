/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization.stax;


import com.cedarsoft.serialization.SerializationException;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Helper class for stax
 */
public class StaxSupport {
  private StaxSupport() {
  }

  @Nonnull
  public static final ThreadLocal<XMLInputFactory> XML_INPUT_FACTORY = new ThreadLocal<XMLInputFactory>() {
    /** @noinspection RefusedBequest*/
    @Override
    protected XMLInputFactory initialValue() {
      return XMLInputFactory.newInstance();
    }
  };
  @Nonnull
  public static final ThreadLocal<XMLOutputFactory> XML_OUTPUT_FACTORY = new ThreadLocal<XMLOutputFactory>() {
    /** @noinspection RefusedBequest*/
    @Override
    protected XMLOutputFactory initialValue() {
      return XMLOutputFactory.newInstance();
    }
  };

  /**
   * Returns a cached xml output factory
   *
   * @return the xml output factory
   */
  @Nonnull
  public static XMLOutputFactory getXmlOutputFactory() {
    return XML_OUTPUT_FACTORY.get();
  }

  /**
   * Returns a cached xml input factory
   *
   * @return the xml input factory
   */
  @Nonnull
  public static XMLInputFactory getXmlInputFactory() {
    return XML_INPUT_FACTORY.get();
  }

  /**
   * Returns the name for an event id
   *
   * @param eventId the event id
   * @return the name
   */
  @Nonnull

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

  public static void clear() {
    StaxSupport.XML_INPUT_FACTORY.remove();
    StaxSupport.XML_OUTPUT_FACTORY.remove();
  }

  public static void enableJson() {
    try {
      XML_INPUT_FACTORY.set( ( XMLInputFactory ) Class.forName( "org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory" ).newInstance() );
      XML_OUTPUT_FACTORY.set( ( XMLOutputFactory ) Class.forName( "org.codehaus.jettison.badgerfish.BadgerFishXMLOutputFactory" ).newInstance() );
    } catch ( Exception e ) {
      throw new SerializationException( e, SerializationException.Details.XML_EXCEPTION, e.getMessage() );
    }
  }

  public static boolean isJsonEnabled() {
    XMLInputFactory factory = XML_INPUT_FACTORY.get();
    return factory.getClass().getName().equals( "org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory" );
  }
}
