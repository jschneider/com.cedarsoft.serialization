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

package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Abstract base class for XML based serializers.
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractXmlSerializerTest<T> extends AbstractSerializerTest<T> {
  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws Exception {
    String expectedWithNamespace = addNameSpace( getExpectedSerialized(), ( AbstractXmlSerializer<?, ?, ?, ?> ) getSerializer() );
    AssertUtils.assertXMLEqual( new String( serialized ), expectedWithNamespace );
  }

  @NotNull
  @NonNls
  public static String addNameSpace( @NotNull @NonNls String xml, @NotNull AbstractXmlSerializer<?, ?, ?, ?> serializer ) throws Exception {
    return addNameSpace( xml, serializer.createNameSpaceUri( serializer.getFormatVersion() ) );
  }

  public static String addNameSpace( @NotNull @NonNls String xml, @NotNull @NonNls String nameSpaceUri ) throws JDOMException, IOException {
    Document doc = new SAXBuilder().build( new ByteArrayInputStream( xml.getBytes() ) );

    Element root = doc.getRootElement();
    if ( root.getNamespaceURI().length() == 0 ) {
      Namespace namespace = Namespace.getNamespace( nameSpaceUri );

      addNameSpaceRecursively( root, namespace );
    }

    return new XMLOutputter( Format.getPrettyFormat() ).outputString( doc );
  }

  public static void addNameSpaceRecursively( @NotNull Element element, @NotNull Namespace namespace ) {
    element.setNamespace( namespace );
    for ( Element child : ( ( Iterable<? extends Element> ) element.getChildren() ) ) {
      addNameSpaceRecursively( child, namespace );
    }
  }

  /**
   * Returns the expected serialized string
   *
   * @return the expected serialized string
   */
  @NotNull
  @NonNls
  protected abstract String getExpectedSerialized();
}
