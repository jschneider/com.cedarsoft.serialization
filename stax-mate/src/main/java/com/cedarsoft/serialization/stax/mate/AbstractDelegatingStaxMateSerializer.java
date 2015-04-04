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

package com.cedarsoft.serialization.stax.mate;

import com.cedarsoft.serialization.SerializationException;
import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.SerializingStrategySupport;
import com.cedarsoft.serialization.VersionMapping;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Abstract base class for a serializers that uses strategies to serialize objects.
 *
 * @param <T> the type
 */
public abstract class AbstractDelegatingStaxMateSerializer<T> extends AbstractStaxMateSerializer<T> {
  @Nonnull

  private static final String ATTRIBUTE_TYPE = "type";
  @Nonnull
  protected final SerializingStrategySupport<T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream> serializingStrategySupport;

  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the format version name
   */
  protected AbstractDelegatingStaxMateSerializer( @Nonnull String defaultElementName, @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
    serializingStrategySupport = new SerializingStrategySupport<T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream>( formatVersionRange );
  }

  @Override
  public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException {
    assert isVersionWritable( formatVersion );

    try {
      SerializingStrategy<T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream> strategy = serializingStrategySupport.findStrategy( object );
      Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
      serializeTo.addAttribute( ATTRIBUTE_TYPE, strategy.getId() );

      strategy.serialize( serializeTo, object, resolvedVersion );
    } catch ( XMLStreamException e ) {
      throw new SerializationException( e, e.getLocation(), SerializationException.Details.XML_EXCEPTION, e.getMessage() );
    }
  }

  @Override
  @Nonnull
  public T deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    String type = deserializeFrom.getAttributeValue( null, ATTRIBUTE_TYPE );

    if ( type == null ) {
      throw new SerializationException( SerializationException.Details.NO_TYPE_ATTRIBUTE );
    }

    SerializingStrategy<? extends T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream> strategy = serializingStrategySupport.findStrategy( type );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    return strategy.deserialize( deserializeFrom, resolvedVersion );
  }

  /**
   * Returns the strategies
   *
   * @return the strategies
   */
  @Nonnull
  public Collection<? extends SerializingStrategy<? extends T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }

  @Nonnull
  public VersionMapping addStrategy( @Nonnull SerializingStrategy<? extends T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream> strategy ) {
    return serializingStrategySupport.addStrategy( strategy );
  }

  @Nonnull
  public SerializingStrategySupport<T, SMOutputElement, XMLStreamReader, XMLStreamException, OutputStream, InputStream> getSerializingStrategySupport() {
    return serializingStrategySupport;
  }
}

