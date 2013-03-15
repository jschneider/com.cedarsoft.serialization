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
package com.cedarsoft.serialization.test.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class XmlNamespaceTranslator {
  @Nonnull
  private Map<Key<String>, Value<String>> translations = new HashMap<Key<String>, Value<String>>();

  public XmlNamespaceTranslator addTranslation( @Nullable String fromNamespaceURI, @Nonnull String toNamespaceURI ) {
    Key<String> key = new Key<String>( fromNamespaceURI );
    Value<String> value = new Value<String>( toNamespaceURI );

    this.translations.put( key, value );

    return this;
  }

  public void translateNamespaces( @Nonnull Document xmlDoc, boolean addNsToAttributes ) {
    Stack<Node> nodes = new Stack<Node>();
    nodes.push( xmlDoc.getDocumentElement() );

    while ( !nodes.isEmpty() ) {
      Node node = nodes.pop();
      switch ( node.getNodeType() ) {
        case Node.ATTRIBUTE_NODE:
        case Node.ELEMENT_NODE:
          Value<String> value = this.translations.get( new Key<String>( node.getNamespaceURI() ) );
          if ( value != null ) {
            // the reassignment to node is very important. as per javadoc renameNode will
            // try to modify node (first parameter) in place. If that is not possible it
            // will replace that node for a new created one and return it to the caller.
            // if we did not reassign node we will get no childs in the loop below.
            node = xmlDoc.renameNode( node, value.getValue(), node.getNodeName() );
          }
          break;
      }

      if ( addNsToAttributes ) {
        // for attributes of this node
        NamedNodeMap attributes = node.getAttributes();
        if ( !( attributes == null || attributes.getLength() == 0 ) ) {
          for ( int i = 0, count = attributes.getLength(); i < count; ++i ) {
            Node attribute = attributes.item( i );
            if ( attribute != null ) {
              nodes.push( attribute );
            }
          }
        }
      }

      // for child nodes of this node
      NodeList childNodes = node.getChildNodes();
      if ( !( childNodes == null || childNodes.getLength() == 0 ) ) {
        for ( int i = 0, count = childNodes.getLength(); i < count; ++i ) {
          Node childNode = childNodes.item( i );
          if ( childNode != null ) {
            nodes.push( childNode );
          }
        }
      }
    }
  }

  // these will allow null values to be stored on a map so that we can distinguish
  // from values being on the map or not. map implementation returns null if the there
  // is no map element with a given key. If the value is null there is no way to
  // distinguish from value not being on the map or value being null. these classes
  // remove ambiguity.
  private static class Holder<T> {

    protected final T value;

    public Holder( T value ) {
      this.value = value;
    }

    public T getValue() {
      return value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
      return result;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( this == obj )
        return true;
      if ( obj == null )
        return false;
      if ( getClass() != obj.getClass() )
        return false;
      Holder<?> other = ( Holder<?> ) obj;
      if ( value == null ) {
        if ( other.value != null )
          return false;
      } else if ( !value.equals( other.value ) )
        return false;
      return true;
    }

  }

  private static class Key<T> extends Holder<T> {

    public Key( T value ) {
      super( value );
    }

  }

  private static class Value<T> extends Holder<T> {

    public Value( T value ) {
      super( value );
    }

  }
}
