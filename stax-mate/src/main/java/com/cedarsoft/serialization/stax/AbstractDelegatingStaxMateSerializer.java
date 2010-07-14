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

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.SerializationContext;
import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.SerializingStrategySupport;
import com.cedarsoft.serialization.VersionMapping;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Collection;

/**
 * Abstract base class for a serializers that uses strategies to serialize objects.
 *
 * @param <T> the type
 */
public class AbstractDelegatingStaxMateSerializer<T> extends AbstractStaxMateSerializer<T> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_TYPE = "type";
  @NotNull
  protected final SerializingStrategySupport<T, SMOutputElement, XMLStreamReader, XMLStreamException> serializingStrategySupport;

  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the format version name
   */
  public AbstractDelegatingStaxMateSerializer( @NotNull String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
    serializingStrategySupport = new SerializingStrategySupport<T, SMOutputElement, XMLStreamReader, XMLStreamException>( formatVersionRange );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull T object, @NotNull Version formatVersion, @NotNull SerializationContext context ) throws IOException {
    assert isVersionWritable( formatVersion );

    try {
      SerializingStrategy<T, SMOutputElement, XMLStreamReader, XMLStreamException> strategy = serializingStrategySupport.findStrategy( object );
      Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
      serializeTo.addAttribute( ATTRIBUTE_TYPE, strategy.getId() );

      strategy.serialize( serializeTo, object, resolvedVersion, context );
    } catch ( XMLStreamException e ) {
      throw new IOException( e );
    }
  }

  @Override
  @NotNull
  public T deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    String type = deserializeFrom.getAttributeValue( null, ATTRIBUTE_TYPE );

    if ( type == null ) {
      throw new XMLStreamException( "No type attribute found. Cannot find strategy." );
    }

    SerializingStrategy<? extends T, SMOutputElement, XMLStreamReader, XMLStreamException> strategy = serializingStrategySupport.findStrategy( type );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    return strategy.deserialize( deserializeFrom, resolvedVersion, context );
  }

  /**
   * Returns the strategies
   *
   * @return the strategies
   */
  @NotNull
  public Collection<? extends SerializingStrategy<? extends T, SMOutputElement, XMLStreamReader, XMLStreamException>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }

  @NotNull
  public VersionMapping addStrategy( @NotNull SerializingStrategy<? extends T, SMOutputElement, XMLStreamReader, XMLStreamException> strategy ) {
    return serializingStrategySupport.addStrategy( strategy );
  }

  @NotNull
  public SerializingStrategySupport<T, SMOutputElement, XMLStreamReader, XMLStreamException> getSerializingStrategySupport() {
    return serializingStrategySupport;
  }
}

