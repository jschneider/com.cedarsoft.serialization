/**
 * Copyright (C) 2010 cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce.txt
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

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.SerializingStrategySupport;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @param <T> the type
 */
public class AbstractDelegatingJDomSerializer<T> extends AbstractJDomSerializer<T> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_TYPE = "type";
  @NotNull
  private final SerializingStrategySupport<T, JDomSerializingStrategy<T>> serializingStrategySupport;

  public AbstractDelegatingJDomSerializer( @NotNull String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange, @NotNull JDomSerializingStrategy<? extends T>... strategies ) {
    this( defaultElementName, nameSpaceUriBase, formatVersionRange, Arrays.asList( strategies ) );
  }

  public AbstractDelegatingJDomSerializer( @NotNull String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange, @NotNull Collection<? extends JDomSerializingStrategy<? extends T>> strategies ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
    this.serializingStrategySupport = new SerializingStrategySupport<T, JDomSerializingStrategy<T>>( strategies );
  }

  @Override
  public void serialize( @NotNull Element serializeTo, @NotNull T object ) throws IOException {
    JDomSerializingStrategy<T> strategy = serializingStrategySupport.findStrategy( object );
    serializeTo.setAttribute( ATTRIBUTE_TYPE, strategy.getId() );
    strategy.serialize( serializeTo, object );
  }

  @Override
  @NotNull
  public T deserialize( @NotNull Element deserializeFrom, @NotNull Version formatVersion ) throws IOException {
    String type = deserializeFrom.getAttributeValue( ATTRIBUTE_TYPE );

    JDomSerializingStrategy<T> strategy = serializingStrategySupport.findStrategy( type );
    return strategy.deserialize( deserializeFrom, formatVersion );
  }

  @NotNull
  public Collection<? extends JDomSerializingStrategy<T>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }
}
