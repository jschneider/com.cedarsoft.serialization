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
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;
import com.cedarsoft.serialization.InvalidNamespaceException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for stax based serializers.
 * <p/>
 * ATTENTION:
 * Serializers based on stax must consume all events for their tag (including END_ELEMENT).<br/>
 * This is especially true for {@link com.cedarsoft.serialization.PluggableSerializer}s.
 *
 * @param <T> the type
 * @param <S> the object to serialize to
 */
public abstract class AbstractStaxBasedSerializer<T, S> extends AbstractXmlSerializer<T, S, XMLStreamReader, XMLStreamException> {
  /**
   * Creates a new serializer
   *
   * @param defaultElementName the name for the root element, if this serializers is not used as delegate. For delegating serializers that value is not used.
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the supported format version range. The upper bound represents the format that is written. All Versions within the range can be read.
   */
  protected AbstractStaxBasedSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  @NotNull
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    try {
      XMLStreamReader reader = StaxSupport.getXmlInputFactory().createXMLStreamReader( in );

      int result = reader.nextTag();
      if ( result != XMLStreamReader.START_ELEMENT ) {
        throw new XMLStreamException( "Expected START_ELEMENT but was <" + result + ">" );
      }

      //Now build the deserialization context
      T deserialized = deserialize( reader, parseAndVerifyNameSpace( reader.getNamespaceURI() ) );

      if ( !reader.isEndElement() ) {
        throw new XMLStreamException( "Not consumed everything in <" + getClass().getName() + ">" );
      }
      if ( reader.next() != XMLStreamReader.END_DOCUMENT ) {
        throw new XMLStreamException( "Not consumed everything in <" + getClass().getName() + ">" );
      }

      return deserialized;
    } catch ( XMLStreamException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    } catch ( InvalidNamespaceException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    }
  }

  /**
   * Ensures that the current tag equals the given tag name
   *
   * @param streamReader the stream reader
   * @param tagName      the tag name
   */
  protected void ensureTag( @NotNull XMLStreamReader streamReader, @NotNull @NonNls String tagName ) throws XMLStreamException {
    ensureTag( streamReader, tagName, null );
  }

  /**
   * Ensures that the current tag equals the given tag name and namespace
   *
   * @param streamReader the stream reader
   * @param tagName      the tag name
   * @param namespace    the (optional) namespace (if the ns is null, no check will be performed)
   */
  protected void ensureTag( @NotNull XMLStreamReader streamReader, @NotNull @NonNls String tagName, @Nullable @NonNls String namespace ) throws XMLStreamException {
    QName qName = streamReader.getName();

    if ( !doesNamespaceFit( streamReader, namespace ) ) {
      throw new XMLStreamException( "Invalid namespace for <" + qName.getLocalPart() + ">. Was <" + qName.getNamespaceURI() + "> but expected <" + namespace + ">" );
    }

    String current = qName.getLocalPart();
    if ( !current.equals( tagName ) ) {
      throw new XMLStreamException( "Invalid tag. Was <" + current + "> but expected <" + tagName + ">" );
    }
  }

  /**
   * Returns whether the namespace fits
   *
   * @param streamReader the stream reader that is used to extract the namespace
   * @param namespace    the expected namespace (or null if the check shall be skipped)
   * @return true if the namespace fits (or the expected namespace is null), false otherwise
   */
  protected boolean doesNamespaceFit( @NotNull XMLStreamReader streamReader, @Nullable @NonNls String namespace ) {
    if ( namespace == null ) {
      return true;
    }

    QName qName = streamReader.getName();
    String nsUri = qName.getNamespaceURI();
    return nsUri.equals( namespace );
  }

  /**
   * Returns the text and closes the tag
   *
   * @param reader the reader
   * @return the text
   *
   * @throws XMLStreamException
   */
  @NotNull
  protected String getText( @NotNull XMLStreamReader reader ) throws XMLStreamException {
    StringBuilder content = new StringBuilder();

    int result;
    while ( ( result = reader.next() ) != XMLStreamReader.END_ELEMENT ) {
      if ( result != XMLStreamReader.CHARACTERS ) {
        throw new XMLStreamException( "Invalid result: " + result );
      }
      content.append( reader.getText() );
    }

    return content.toString();
  }

  /**
   * Returns the child text
   *
   * @param reader  the reader
   * @param tagName the tag name
   * @return the text of the child with the given tag name
   *
   * @throws XMLStreamException
   */
  @NotNull
  protected String getChildText( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName ) throws XMLStreamException {
    return getChildText( reader, tagName, null );
  }

  /**
   * Returns the child text
   *
   * @param reader    the reader
   * @param tagName   the tag name
   * @param namespace the (optional) namespace that is only verified, if it is not null
   * @return the text of the child with the given tag name
   *
   * @throws XMLStreamException
   */
  @NotNull
  protected String getChildText( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName, @Nullable @NonNls String namespace ) throws XMLStreamException {
    nextTag( reader, tagName );
    ensureTag( reader, tagName, namespace );
    return getText( reader );
  }

  /**
   * Closes the current tag
   *
   * @param reader the reader
   * @throws XMLStreamException
   */
  protected void closeTag( @NotNull XMLStreamReader reader ) throws XMLStreamException {
    closeTag( reader, true );
  }

  /**
   * Closes the tag
   *
   * @param reader the reader
   * @param skipElementsWithOtherNamespaces
   *               whether tos kip elements with other namespaces
   * @throws XMLStreamException
   */
  protected void closeTag( @NotNull XMLStreamReader reader, boolean skipElementsWithOtherNamespaces ) throws XMLStreamException {
    int result = reader.nextTag();
    if ( result == XMLStreamReader.END_ELEMENT ) {
      return;
    }

    if ( !skipElementsWithOtherNamespaces || result != XMLStreamReader.START_ELEMENT ) {
      throw new XMLStreamException( "Invalid result. Expected <END_ELEMENT> but was <" + StaxSupport.getEventName( result ) + ">" );
    }

    if ( doesNamespaceFit( reader, getNameSpaceUri() ) ) {
      throw new XMLStreamException( "Invalid result. Expected <END_ELEMENT> but was <" + StaxSupport.getEventName( result ) + ">" );
    }

    skipCurrentTag( reader );
    closeTag( reader );
  }

  /**
   * Opens the next tag
   *
   * @param reader  the reader
   * @param tagName the tag name
   * @throws XMLStreamException
   */
  protected void nextTag( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName ) throws XMLStreamException {
    nextTag( reader, tagName, null );
  }

  /**
   * Opens the next tag
   *
   * @param reader    the reader
   * @param tagName   the tag name
   * @param namespace the (optional) namespace (if the ns is null, no check will be performed)
   * @throws XMLStreamException
   */
  protected void nextTag( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName, @Nullable @NonNls String namespace ) throws XMLStreamException {
    nextTag( reader, tagName, namespace, true );
  }

  /**
   * Opens the next tag
   *
   * @param reader    the reader
   * @param tagName   the tag name
   * @param namespace the (optional) namespace (if the ns is null, no check will be performed)
   * @param skipElementsWithOtherNamespaces
   *                  whether to skip unknown namespaces
   * @throws XMLStreamException
   */
  protected void nextTag( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName, @Nullable @NonNls String namespace, boolean skipElementsWithOtherNamespaces ) throws XMLStreamException {
    int result = reader.nextTag();
    if ( result != XMLStreamReader.START_ELEMENT ) {
      throw new XMLStreamException( "Invalid result. Expected <START_ELEMENT> but was <" + StaxSupport.getEventName( result ) + ">" );
    }

    if ( skipElementsWithOtherNamespaces && !doesNamespaceFit( reader, namespace ) ) {
      skipCurrentTag( reader );
      nextTag( reader, tagName, namespace, skipElementsWithOtherNamespaces );
    } else {
      ensureTag( reader, tagName, namespace );
    }
  }

  /**
   * Skips the current tag (will skip all children and close the tag itself)
   *
   * @param reader the reader
   * @throws XMLStreamException
   */
  protected void skipCurrentTag( @NotNull XMLStreamReader reader ) throws XMLStreamException {
    int counter = 1;

    while ( counter > 0 ) {
      int result = reader.next();

      if ( result == XMLStreamReader.END_ELEMENT ) {
        counter--;
      } else if ( result == XMLStreamReader.START_ELEMENT ) {
        counter++;
      }
    }
  }

  /**
   * Attention! The current element will be closed!
   *
   * @param streamReader the stream reader
   * @param callback     the callback
   * @throws XMLStreamException
   * @throws IOException
   */
  protected void visitChildren( @NotNull XMLStreamReader streamReader, @NotNull CB callback ) throws XMLStreamException, IOException {
    while ( streamReader.nextTag() != XMLStreamReader.END_ELEMENT ) {
      String tagName = streamReader.getName().getLocalPart();
      callback.tagEntered( streamReader, tagName );
    }
  }

  /**
   * Deserializes a collection
   *
   * @param deserializeFrom where it is deserialized from
   * @param type            the type
   * @param formatVersion   the format version
   * @return the deserialized objects
   *
   * @throws XMLStreamException
   * @throws IOException
   */
  @NotNull
  protected <T> List<? extends T> deserializeCollection( @NotNull XMLStreamReader deserializeFrom, @NotNull final Class<T> type, @NotNull final Version formatVersion ) throws XMLStreamException, IOException {
    final List<T> deserializedObjects = new ArrayList<T>();

    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
        deserializedObjects.add( deserialize( type, formatVersion, deserializeFrom ) );
      }
    } );

    return deserializedObjects;
  }

  /**
   * Convenience method to deserialize multiple collections without having to deal with callbacks
   *
   * @param deserializeFrom    the object to deserialize from
   * @param formatVersion      the format version
   * @param collectionsMapping the collections mapping that holds references to the collections that are filled
   * @throws XMLStreamException
   * @throws IOException
   */
  protected void deserializeCollections( @NotNull final XMLStreamReader deserializeFrom, @NotNull final Version formatVersion, final CollectionsMapping collectionsMapping ) throws XMLStreamException, IOException {
    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
        CollectionsMapping.Entry entry = collectionsMapping.getEntry( tagName );
        Object deserialized = deserialize( entry.getType(), formatVersion, deserializeFrom );
        entry.getTargetCollection().add( deserialized );
      }
    } );
  }

  /**
   * Callback interface used when visiting the children ({@link com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer#visitChildren(XMLStreamReader, com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer.CB)})
   */
  public interface CB {
    /**
     * Is called for each child.
     * ATTENTION: This method *must* close the tag
     *
     * @param deserializeFrom the reader
     * @param tagName         the tag name
     * @throws XMLStreamException
     * @throws IOException
     */
    void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException;
  }
}
