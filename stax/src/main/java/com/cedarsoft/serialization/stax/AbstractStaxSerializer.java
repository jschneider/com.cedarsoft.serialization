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
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

/**
 * Abstract base class for serializer using stax.
 *
 * @param <T> the type
 */
public abstract class AbstractStaxSerializer<T> extends AbstractStaxBasedSerializer<T, XMLStreamWriter> {
  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the format version range
   */
  protected AbstractStaxSerializer( @Nonnull String defaultElementName, @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @Nonnull T object, @Nonnull OutputStream out ) throws IOException {
    try {
      XMLOutputFactory xmlOutputFactory = StaxSupport.getXmlOutputFactory();
      XMLStreamWriter writer = wrapWithIndent(xmlOutputFactory.createXMLStreamWriter(out));

      //Sets the name space
      String nameSpace = getNameSpace();
      writer.setDefaultNamespace( nameSpace );

      writer.writeStartElement( getDefaultElementName() );
      writer.writeDefaultNamespace( nameSpace );

      serialize( writer, object, getFormatVersion() );
      writer.writeEndElement();

      writer.close();
    } catch ( XMLStreamException e ) {
      throw new SerializationException( e, e.getLocation(), SerializationException.Details.XML_EXCEPTION, e.getMessage() );
    }
  }

  @Nullable
  private static final Constructor<?> INDENTING_WRITER_CONSTRUCTOR = getIndentingConstructor();

  @Nullable
  private static Constructor<?> getIndentingConstructor() {
    try {
      Class<?> indentingType = Class.forName("com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter");
      return indentingType.getConstructor(XMLStreamWriter.class);
    } catch (Exception ignore) {
      return null;
    }
  }

  @Nonnull
  protected static XMLStreamWriter wrapWithIndent(@Nonnull XMLStreamWriter xmlStreamWriter) {
    if (INDENTING_WRITER_CONSTRUCTOR == null) {
      return xmlStreamWriter;
    }

    try {
      return (XMLStreamWriter) INDENTING_WRITER_CONSTRUCTOR.newInstance(xmlStreamWriter);
    } catch (Exception ignore) {
      //We could not instantiate the writer
      return xmlStreamWriter;
    }
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
  protected <T> void serializeCollection( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull String elementName, @Nonnull XMLStreamWriter serializeTo, @Nonnull Version formatVersion ) throws XMLStreamException, IOException {
    AbstractXmlSerializer<? super T, XMLStreamWriter, XMLStreamReader, XMLStreamException> serializer = getSerializer( type );
    Version resolvedVersion = getDelegatesMappings().resolveVersion( type, formatVersion );

    for ( T object : objects ) {
      serializeTo.writeStartElement( elementName );
      serializer.serialize( serializeTo, object, resolvedVersion );
      serializeTo.writeEndElement();
    }
  }

  protected <T> void serializeCollection( @Nonnull Iterable<? extends T> objects, @Nonnull Class<T> type, @Nonnull XMLStreamWriter serializeTo, @Nonnull Version formatVersion ) throws XMLStreamException, IOException {
    AbstractXmlSerializer<? super T, XMLStreamWriter, XMLStreamReader, XMLStreamException> serializer = getSerializer( type );
    Version resolvedVersion = getDelegatesMappings().resolveVersion( type, formatVersion );

    for ( T object : objects ) {
      serializeTo.writeStartElement( serializer.getDefaultElementName() );
      serializer.serialize( serializeTo, object, resolvedVersion );
      serializeTo.writeEndElement();
    }
  }
}
