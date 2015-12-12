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
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;

import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for stax based serializers.
 * <p>
 * ATTENTION:
 * Serializers based on stax must consume all events for their tag (including END_ELEMENT).<br>
 * This is especially true for com.cedarsoft.serialization.PluggableSerializers.
 *
 * @param <T> the type
 * @param <S> the object to serialize to
 */
public abstract class AbstractStaxBasedSerializer<T, S> extends AbstractXmlSerializer<T, S, XMLStreamReader, XMLStreamException> implements StaxBasedSerializer<T, S> {
  /**
   * Creates a new serializer
   *
   * @param defaultElementName the name for the root element, if this serializers is not used as delegate. For delegating serializers that value is not used.
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the supported format version range. The upper bound represents the format that is written. All Versions within the range can be read.
   */
  protected AbstractStaxBasedSerializer( @Nonnull String defaultElementName, @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  @Nonnull
  public T deserialize( @Nonnull InputStream in ) throws IOException, VersionException {
    try {
      XMLStreamReader reader = StaxSupport.getXmlInputFactory().createXMLStreamReader( in );

      int result = reader.nextTag();
      if ( result != XMLStreamConstants.START_ELEMENT ) {
        throw new SerializationException( reader.getLocation(), SerializationException.Details.INVALID_START_ELEMENT, result );
      }

      //Now build the deserialization context
      T deserialized = deserialize( reader, parseAndVerifyNameSpace( reader.getNamespaceURI() ) );

      if ( !reader.isEndElement() ) {
        throw new SerializationException( reader.getLocation(), SerializationException.Details.NOT_CONSUMED_EVERYTHING, getClass().getName() );
      }
      if (nextEndDocument(reader)) {
        throw new SerializationException( reader.getLocation(), SerializationException.Details.NOT_CONSUMED_EVERYTHING, getClass().getName() );
      }

      return deserialized;
    } catch ( XMLStreamException e ) {
      throw new SerializationException(e, e.getLocation(), SerializationException.Details.XML_EXCEPTION, e.getMessage() );
    }
  }

  /**
   * Ensures that the current tag equals the given tag name
   *
   * @param streamReader the stream reader
   * @param tagName      the tag name
   */
  protected void ensureTag( @Nonnull XMLStreamReader streamReader, @Nonnull String tagName ) throws XMLStreamException {
    ensureTag( streamReader, tagName, null );
  }

  /**
   * Ensures that the current tag equals the given tag name and namespace
   *
   * @param streamReader the stream reader
   * @param tagName      the tag name
   * @param namespace    the (optional) namespace (if the ns is null, no check will be performed)
   */
  protected void ensureTag( @Nonnull XMLStreamReader streamReader, @Nonnull String tagName, @Nullable String namespace ) throws XMLStreamException {
    QName qName = streamReader.getName();

    if ( !doesNamespaceFit( streamReader, namespace ) ) {
      throw new XMLStreamException( "Invalid namespace for <" + qName.getLocalPart() + ">. Was <" + qName.getNamespaceURI() + "> but expected <" + namespace + "> @ " + streamReader.getLocation() );
    }

    String current = qName.getLocalPart();
    if ( !current.equals( tagName ) ) {
      throw new XMLStreamException( "Invalid tag. Was <" + current + "> but expected <" + tagName + "> @ " + streamReader.getLocation() );
    }
  }

  /**
   * Returns whether the namespace fits
   *
   * @param streamReader the stream reader that is used to extract the namespace
   * @param namespace    the expected namespace (or null if the check shall be skipped)
   * @return true if the namespace fits (or the expected namespace is null), false otherwise
   */
  protected boolean doesNamespaceFit( @Nonnull XMLStreamReader streamReader, @Nullable String namespace ) {
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
  @Nonnull
  public static String getText( @Nonnull XMLStreamReader reader ) throws XMLStreamException {
    StringBuilder content = new StringBuilder();

    int result;
    while ( ( result = reader.next() ) != XMLStreamConstants.END_ELEMENT ) {
      if ( result != XMLStreamConstants.CHARACTERS ) {
        throw new XMLStreamException( "Invalid result: " + result + " @ " + reader.getLocation() );
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
  @Nonnull
  protected String getChildText( @Nonnull XMLStreamReader reader, @Nonnull String tagName ) throws XMLStreamException {
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
  @Nonnull
  protected String getChildText( @Nonnull XMLStreamReader reader, @Nonnull String tagName, @Nullable String namespace ) throws XMLStreamException {
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
  protected void closeTag( @Nonnull XMLStreamReader reader ) throws XMLStreamException {
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
  protected void closeTag( @Nonnull XMLStreamReader reader, boolean skipElementsWithOtherNamespaces ) throws XMLStreamException {
    int result = reader.nextTag();
    if ( result == XMLStreamConstants.END_ELEMENT ) {
      return;
    }

    if ( !skipElementsWithOtherNamespaces || result != XMLStreamConstants.START_ELEMENT ) {
      throw new XMLStreamException( "Invalid result. Expected <END_ELEMENT> but was <" + StaxSupport.getEventName( result ) + "> @ " + reader.getLocation() );
    }

    if ( doesNamespaceFit( reader, getNameSpace() ) ) {
      throw new XMLStreamException( "Invalid result. Expected <END_ELEMENT> but was <" + StaxSupport.getEventName( result ) + "> @ " + reader.getLocation() );
    }

    skipCurrentTag( reader );
    closeTag( reader );
  }

  protected boolean nextEndDocument(@Nonnull XMLStreamReader reader) throws XMLStreamException {
    int next = reader.next();
    //Skip comments
    while (next == XMLStreamConstants.COMMENT) {
      next = reader.next();
    }

    return next != XMLStreamConstants.END_DOCUMENT;
  }

  /**
   * Opens the next tag
   *
   * @param reader  the reader
   * @param tagName the tag name
   * @throws XMLStreamException
   */
  protected void nextTag( @Nonnull XMLStreamReader reader, @Nonnull String tagName ) throws XMLStreamException {
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
  protected void nextTag( @Nonnull XMLStreamReader reader, @Nonnull String tagName, @Nullable String namespace ) throws XMLStreamException {
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
  protected void nextTag( @Nonnull XMLStreamReader reader, @Nonnull String tagName, @Nullable String namespace, boolean skipElementsWithOtherNamespaces ) throws XMLStreamException {
    int result = reader.nextTag();
    if ( result != XMLStreamConstants.START_ELEMENT ) {
      throw new XMLStreamException( "Invalid result. Expected <START_ELEMENT> but was <" + StaxSupport.getEventName( result ) + ">" + " @ " + reader.getLocation() );
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
  protected void skipCurrentTag( @Nonnull XMLStreamReader reader ) throws XMLStreamException {
    int counter = 1;

    while ( counter > 0 ) {
      int result = reader.next();

      if ( result == XMLStreamConstants.END_ELEMENT ) {
        counter--;
      } else if ( result == XMLStreamConstants.START_ELEMENT ) {
        counter++;
      }
    }
  }

  /**
   * Attention! The current element will be closed!
   *
   * @param streamReader the stream reader
   * @param callback     the callback
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  protected void visitChildren( @Nonnull XMLStreamReader streamReader, @Nonnull CB callback ) throws XMLStreamException, IOException {
    while ( streamReader.nextTag() != XMLStreamConstants.END_ELEMENT ) {
      String tagName = streamReader.getName().getLocalPart();
      callback.tagEntered( streamReader, tagName );
    }
  }

  /**
   * Convenience method
   *
   * @param deserializeFrom where it is deserialized from
   * @param type            the type
   * @param formatVersion   the format version
   * @return the deserialized objects
   *
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  @Nonnull
  protected <L> List<? extends L> deserializeCollection( @Nonnull XMLStreamReader deserializeFrom, @Nonnull final Class<L> type, @Nonnull final Version formatVersion ) throws XMLStreamException, IOException {
    return deserializeCollection( type, formatVersion, deserializeFrom );
  }

  /**
   * Deserializes a collection
   *
   * @param type the type
   * @param formatVersion the format version
   * @param deserializeFrom deserialize from
   * @param <L> the type
   * @return the deserialized collection
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  @Nonnull
  protected <L> List<? extends L> deserializeCollection( @Nonnull final Class<L> type, @Nonnull final Version formatVersion, @Nonnull XMLStreamReader deserializeFrom ) throws XMLStreamException, IOException {
    final List<L> deserializedObjects = new ArrayList<L>();

    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( @Nonnull XMLStreamReader deserializeFrom, @Nonnull String tagName ) throws XMLStreamException, IOException {
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
   * @throws XMLStreamException if there is an xml problem
   * @throws IOException if there is an io problem
   */
  protected void deserializeCollections( @Nonnull final XMLStreamReader deserializeFrom, @Nonnull final Version formatVersion, final CollectionsMapping collectionsMapping ) throws XMLStreamException, IOException {
    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( @Nonnull XMLStreamReader deserializeFrom, @Nonnull String tagName ) throws XMLStreamException, IOException {
        CollectionsMapping.Entry entry = collectionsMapping.getEntry( tagName );
        Object deserialized = deserialize( entry.getType(), formatVersion, deserializeFrom );
        entry.getTargetCollection().add( deserialized );
      }
    } );
  }

  /**
   * Deserializes the enum
   *
   * @param enumType        the enum type
   * @param propertyName    the property name
   * @param deserializeFrom the object to deserialize from
   * @param <T>             the type
   * @return the deserialized enum
   */
  @Nonnull
  public <T extends Enum<T>> T deserializeEnum( @Nonnull Class<T> enumType, @Nonnull String propertyName, @Nonnull XMLStreamReader deserializeFrom ) {
    String enumValue = deserializeFrom.getAttributeValue( null, propertyName );
    return Enum.valueOf( enumType, enumValue );
  }

  /**
   * Callback interface used when visiting the children (com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer#visitChildren(XMLStreamReader, com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer.CB))
   */
  public interface CB {
    /**
     * Is called for each child.
     * ATTENTION: This method *must* close the tag
     *
     * @param deserializeFrom the reader
     * @param tagName         the tag name
     * @throws XMLStreamException if there is an xml problem
     * @throws IOException if there is an io problem
     */
    void tagEntered( @Nonnull XMLStreamReader deserializeFrom, @Nonnull String tagName ) throws XMLStreamException, IOException;
  }
}
