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
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.SerializationContext;
import com.cedarsoft.serialization.SerializingStrategySupport;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class SerializingStrategySupportTest {
  @Test
  public void testGenerics() {
    SerializingStrategySupport<String, MyStrategy> support = new SerializingStrategySupport<String, MyStrategy>( Arrays.asList( new MyStrategy() ) );
    SerializingStrategySupport<String, StaxMateSerializingStrategy<String>> support2 = new SerializingStrategySupport<String, StaxMateSerializingStrategy<String>>( Arrays.asList( new MyStrategy() ) );

    List<StaxMateSerializingStrategy<? extends String>> strategies1 = Arrays.<StaxMateSerializingStrategy<? extends String>>asList( new MyStrategy() );

    SerializingStrategySupport<String, StaxMateSerializingStrategy<String>> support5 = new SerializingStrategySupport<String, StaxMateSerializingStrategy<String>>( strategies1 );

    Collection<? extends StaxMateSerializingStrategy<? extends String>> strategies = new ArrayList<StaxMateSerializingStrategy<? extends String>>();
    try {
      SerializingStrategySupport<String, StaxMateSerializingStrategy<String>> support3 = new SerializingStrategySupport<String, StaxMateSerializingStrategy<String>>( strategies );
    } catch ( Exception ignore ) {
    }
  }

  private static class MyStrategy implements StaxMateSerializingStrategy<String> {
    @Override
    @NotNull
    public String getId() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void serialize( @NotNull String object, @NotNull OutputStream out ) throws IOException {
      throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String deserialize( @NotNull InputStream in ) throws IOException, VersionException {
      throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Version getFormatVersion() {
      throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public VersionRange getFormatVersionRange() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports( @NotNull Object object ) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull String object, @NotNull SerializationContext context ) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public String deserialize( @NotNull @NonNls XMLStreamReader deserializeFrom, @NotNull Version formatVersion, @NotNull DeserializationContext context ) throws IOException {
      throw new UnsupportedOperationException();
    }
  }

}
