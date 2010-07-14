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

package com.cedarsoft.serialization.jdom;

import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.InvalidNamespaceException;
import com.cedarsoft.serialization.SerializationContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract serializer based on JDom
 *
 * @param <T> the type
 */
public abstract class AbstractJDomSerializer<T> extends AbstractXmlSerializer<T, Element, Element, IOException> {
  @NotNull
  @NonNls
  protected static final String LINE_SEPARATOR = "\n";

  protected AbstractJDomSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Deprecated
  @NotNull
  public Element serializeToElement( @NotNull T object ) throws IOException {
    Element element = new Element( getDefaultElementName() );
    serialize( element, object, getFormatVersion(), new SerializationContext() );
    return element;
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    Document document = new Document();

    //The name space
    Namespace namespace = Namespace.getNamespace( getNameSpaceUri() );

    //Create the root
    Element root = new Element( getDefaultElementName(), namespace );
    document.setRootElement( root );

    SerializationContext context = new SerializationContext();
    serialize( root, object, getFormatVersion(), context );
    new XMLOutputter( Format.getPrettyFormat().setLineSeparator( LINE_SEPARATOR ) ).output( document, out );
  }

  @Override
  @NotNull
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    try {
      Document document = new SAXBuilder().build( in );

      String namespaceURI = document.getRootElement().getNamespaceURI();
      DeserializationContext context = createDeserializationContext( namespaceURI );

      return deserialize( document.getRootElement(), context.getFormatVersion(), context );
    } catch ( JDOMException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    } catch ( InvalidNamespaceException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    }
  }
}
