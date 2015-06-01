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

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.test.utils.JsonUtils;
import com.cedarsoft.version.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.io.Charsets;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.fest.reflect.core.Reflection;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract base class for JSON based serializers.
 * <p>
 * Attention: it is necessary to define at least one DataPoint:
 * <p>
 * <pre>&#064;DataPoint<br>public static final Entry&lt;?&gt; ENTRY1 = create(<br> new DomainObject(),<br> &quot;{}&quot; );</pre>
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractJsonSerializerTest2<T> extends AbstractSerializerTest2<T> {
  @Nonnull
  @Override
  protected abstract StreamSerializer<T> getSerializer() throws Exception;

  protected void verify( @Nonnull byte[] current, @Nonnull byte[] expectedJson ) throws Exception {
    String expectedAsString = new String( expectedJson, Charsets.UTF_8 );
    if ( addTypeInformation() ) {
      try {
        expectedAsString = addTypeInformation( expectedJson );
      } catch ( Exception e ) {
        System.err.println( "WARNING. Could not add type information due to " + e.getMessage() );
      }
    }

    JsonUtils.assertJsonEquals(expectedAsString, new String(current, Charsets.UTF_8));
  }

  @Nonnull

  public String addTypeInformation( @Nonnull byte[] expectedJson ) throws Exception {
    Serializer<T,?, ?> serializer = getSerializer();
    return addTypeInformation( serializer, expectedJson );
  }

  @Nonnull
  protected static String getType( @Nonnull Serializer<?, ?, ?> serializer ) {
    return Reflection.method( "getType" ).withReturnType( String.class ).in( serializer ).invoke();
  }

  @Nonnull

  public static String addTypeInformation( @Nonnull Serializer<?, ?, ?> serializer, @Nonnull byte[] expectedJson ) throws Exception {
    return addTypeInformation( getType( serializer ), serializer.getFormatVersion(), expectedJson );
  }

  protected boolean addTypeInformation() {
    return true;
  }

  @Nonnull

  public static String addTypeInformation( @Nonnull String type, @Nonnull Version version, @Nonnull byte[] xmlBytes ) throws Exception {
    JsonNode tree = new ObjectMapper().readTree( new String( xmlBytes, Charsets.UTF_8 ) );

    Map<String, JsonNode> newProps = new LinkedHashMap<String, JsonNode>();
    newProps.put( "@type", new TextNode( type ) );
    newProps.put( "@version", new TextNode( version.format() ) );

    Iterator<Map.Entry<String, JsonNode>> nodeIterator = tree.fields();
    while ( nodeIterator.hasNext() ) {
      Map.Entry<String, JsonNode> jsonNode = nodeIterator.next();
      newProps.put( jsonNode.getKey(), jsonNode.getValue() );
    }

    ( ( ContainerNode ) tree ).removeAll();
    ( ( ObjectNode ) tree ).putAll( newProps );

    return tree.toString();
  }

  @Override
  protected void verifySerialized( @Nonnull Entry<T> entry, @Nonnull byte[] serialized ) throws Exception {
    verify( serialized, entry.getExpected() );
  }

  @Nonnull
  protected static <T> Entry<? extends T> create( @Nonnull T object, @Nonnull String expected ) {
    return new Entry<T>( object, expected.getBytes(StandardCharsets.UTF_8) );
  }
}