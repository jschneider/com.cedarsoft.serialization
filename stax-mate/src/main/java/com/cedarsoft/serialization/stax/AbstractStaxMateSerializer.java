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

import com.cedarsoft.VersionRange;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for stax mate based serializers
 *
 * @param <T> the type
 */
public abstract class AbstractStaxMateSerializer<T> extends AbstractStaxBasedSerializer<T, SMOutputElement> {
  protected AbstractStaxMateSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    try {
      SMOutputDocument doc = StaxMateSupport.getSmOutputFactory().createOutputDocument( out );

      String nameSpaceUri = getNameSpaceUri();
      SMNamespace nameSpace = doc.getNamespace( nameSpaceUri );

      SMOutputElement root = doc.addElement( nameSpace, getDefaultElementName() );
      serialize( root, object );
      doc.closeRoot();
    } catch ( XMLStreamException e ) {
      throw new IOException( e );
    }
  }

  /**
   * Serializes the elements of a collection
   *
   * @param objects     the objects that are serialized
   * @param type        the type
   * @param elementName the element name
   * @param serializeTo the object the elements are serialized to
   * @throws XMLStreamException
   * @throws IOException
   */
  protected <T> void serializeCollection( @NotNull Iterable<? extends T> objects, @NotNull Class<T> type, @NotNull @NonNls String elementName, @NotNull SMOutputElement serializeTo ) throws XMLStreamException, IOException {
    for ( T object : objects ) {
      SMOutputElement doorElement = serializeTo.addElement( serializeTo.getNamespace(), elementName );
      getSerializer( type ).serialize( doorElement, object );
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
   * @throws XMLStreamException
   * @throws IOException
   */
  protected <T> void serializeCollectionToElement( @NotNull Iterable<? extends T> objects, @NotNull Class<T> type, @NotNull @NonNls String collectionElementName, @NotNull @NonNls String elementName, @NotNull SMOutputElement serializeTo ) throws XMLStreamException, IOException {
    SMOutputElement collectionElement = serializeTo.addElement( serializeTo.getNamespace(), collectionElementName );
    serializeCollection( objects, type, elementName, collectionElement );
  }

  protected void serializeToElementWithCharacters( @NotNull @NonNls String elementName, @NotNull String characters, @NotNull SMOutputElement serializeTo ) throws XMLStreamException {
    serializeTo.addElementWithCharacters( serializeTo.getNamespace(), elementName, characters );
  }
}
