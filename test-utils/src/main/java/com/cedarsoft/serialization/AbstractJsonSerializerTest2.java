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

import com.cedarsoft.JsonUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract base class for XML based serializers.
 * <p/>
 * <p/>
 * Attention: it is necessary to define at least one DataPoint:
 * <p/>
 * <pre>&#064;DataPoint<br/>public static final Entry&lt;?&gt; ENTRY1 = create(<br/> new DomainObject(),<br/> &quot;{}&quot; );</pre>
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractJsonSerializerTest2<T> extends AbstractSerializerTest2<T> {
  protected void verify( @NonNls @NotNull byte[] current, @NotNull @NonNls byte[] expectedJson ) throws Exception {
    if ( addNameSpace() ) {
      String expectedWithNamespace = addNameSpace( ( ( AbstractNameSpaceBasedSerializer<?, ?, ?, ?> ) getSerializer() ).getNameSpace(), expectedJson );
      JsonUtils.assertJsonEquals( expectedWithNamespace, new String( current ) );
    } else {
      JsonUtils.assertJsonEquals( new String( expectedJson ), new String( current ) );
    }
  }

  protected boolean addNameSpace() {
    return true;
  }

  @NotNull
  @NonNls
  public static String addNameSpace( @NotNull @NonNls String nameSpaceUri, @NotNull @NonNls byte[] xmlBytes ) throws Exception {
    JsonNode tree = new ObjectMapper().readTree( new String( xmlBytes ) );

    Map<String, JsonNode> newProps = new LinkedHashMap<String, JsonNode>();
    newProps.put( "@ns", new TextNode( nameSpaceUri ) );

    Iterator<Map.Entry<String, JsonNode>> nodeIterator = ( ( ObjectNode ) tree ).getFields();
    while ( nodeIterator.hasNext() ) {
      Map.Entry<String, JsonNode> jsonNode = nodeIterator.next();
      newProps.put( jsonNode.getKey(), jsonNode.getValue() );
    }

    ( ( ObjectNode ) tree ).removeAll();
    ( ( ObjectNode ) tree ).putAll( newProps );

    return tree.toString();
  }

  @Override
  protected void verifySerialized( @NotNull Entry<T> entry, @NotNull byte[] serialized ) throws Exception {
    verify( serialized, entry.getExpected() );
  }

  @NotNull
  protected static <T> Entry<? extends T> create( @NotNull T object, @NotNull @NonNls String expected ) {
    return new Entry<T>( object, expected.getBytes() );
  }
}