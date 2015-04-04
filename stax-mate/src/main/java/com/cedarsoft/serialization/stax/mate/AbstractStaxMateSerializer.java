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
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;
import com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for stax mate based serializers
 *
 * @param <T> the type
 */
public abstract class AbstractStaxMateSerializer<T> extends AbstractStaxBasedSerializer<T, SMOutputElement> {
  @Nonnull
  public static final String INDENT_STR = "\n                            ";

  protected AbstractStaxMateSerializer( @Nonnull String defaultElementName, @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @Nonnull T object, @Nonnull OutputStream out ) throws IOException {
    try {
      SMOutputFactory factory = StaxMateSupport.getSmOutputFactory();
      SMOutputDocument doc = factory.createOutputDocument( out );
      if (! StaxMateSupport.isJsonEnabled() ) {
        doc.setIndentation( INDENT_STR, 1, 2 );
      }

      String nameSpaceUri = getNameSpace();
      SMNamespace nameSpace = doc.getNamespace( nameSpaceUri );

      SMOutputElement root = doc.addElement( nameSpace, getDefaultElementName() );
      serialize( root, object, getFormatVersion() );
      doc.closeRoot();
    } catch ( XMLStreamException e ) {
      throw new SerializationException( e, e.getLocation(), SerializationException.Details.XML_EXCEPTION, e.getMessage() );
    }
  }

  /**
   * Serializes the given object into a sub element of serializeTo
   *
   * @param object        the object that is serialized
   * @param type          the type
   * @param subElementName  the name of the sub element
   * @param serializeTo   the parent element
   * @param formatVersion the format version
   * @param <T>           the type
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  public <T> void serialize( @Nonnull T object, @Nonnull Class<T> type, @Nonnull String subElementName, @Nonnull SMOutputElement serializeTo, @Nonnull Version formatVersion ) throws XMLStreamException, IOException {
    SMOutputElement element = serializeTo.addElement( serializeTo.getNamespace(), subElementName );
    serialize( object, type, element, formatVersion );
  }

  /**
   * Serializes the elements of a collection
   *
   * @param objects       the objects that are serialized
   * @param type          the type
   * @param elementName   the element name
   * @param serializeTo   the object the elements are serialized to
   * @param formatVersion the format version
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  protected <T> void serializeCollection( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull String elementName, @Nonnull SMOutputElement serializeTo, @Nonnull Version formatVersion ) throws XMLStreamException, IOException {
    AbstractXmlSerializer<? super T, SMOutputElement, XMLStreamReader, XMLStreamException> serializer = getSerializer( type );
    Version resolvedVersion = getDelegatesMappings().resolveVersion( type, formatVersion );

    for ( T object : objects ) {
      SMOutputElement doorElement = serializeTo.addElement( serializeTo.getNamespace(), elementName );
      serializer.serialize( doorElement, object, resolvedVersion );
    }
  }

  protected <T> void serializeCollection( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull SMOutputElement serializeTo, @Nonnull Version formatVersion ) throws XMLStreamException, IOException {
    AbstractXmlSerializer<? super T, SMOutputElement, XMLStreamReader, XMLStreamException> serializer = getSerializer( type );
    Version resolvedVersion = getDelegatesMappings().resolveVersion( type, formatVersion );

    for ( T object : objects ) {
      SMOutputElement doorElement = serializeTo.addElement( serializeTo.getNamespace(), serializer.getDefaultElementName() );
      serializer.serialize( doorElement, object, resolvedVersion );
    }
  }

  /**
   * Serializes the elements of the collection to a own sub element
   *
   * @param objects               the objects that are serialized
   * @param type                  the type
   * @param collectionElementName the collection element name
   * @param elementName           the element name
   * @param serializeTo           the object the elements are serialized to
   * @param formatVersion         the format version
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  protected <T> void serializeCollectionToElement( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull String collectionElementName, @Nonnull String elementName, @Nonnull SMOutputElement serializeTo, Version formatVersion ) throws XMLStreamException, IOException {
    SMOutputElement collectionElement = serializeTo.addElement( serializeTo.getNamespace(), collectionElementName );
    serializeCollection( objects, type, elementName, collectionElement, formatVersion );
  }

  protected <T> void serializeCollectionToElement( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull String collectionElementName, @Nonnull SMOutputElement serializeTo, Version formatVersion ) throws XMLStreamException, IOException {
    SMOutputElement collectionElement = serializeTo.addElement( serializeTo.getNamespace(), collectionElementName );
    serializeCollection( objects, type, collectionElement, formatVersion );
  }

  protected void serializeToElementWithCharacters( @Nonnull String elementName, @Nonnull String characters, @Nonnull SMOutputElement serializeTo ) throws XMLStreamException {
    serializeTo.addElementWithCharacters( serializeTo.getNamespace(), elementName, characters );
  }

  /**
   * Serializes an enum (as attribute)
   *
   * @param enumValue    the num value
   * @param propertyName the property name
   * @param serializeTo  the object to serialize to
   * @throws XMLStreamException
   */
  public void serializeEnum( @Nonnull Enum<?> enumValue, @Nonnull String propertyName, @Nonnull SMOutputElement serializeTo ) throws XMLStreamException {
    serializeTo.addAttribute( propertyName, enumValue.name() );
  }
}
