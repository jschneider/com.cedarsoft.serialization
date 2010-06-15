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

package com.cedarsoft.serialization.generator.output.serializer;

import com.cedarsoft.serialization.generator.output.ClassRefSupport;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFormatter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.testng.Assert.*;

/**
 *
 */
public class NewInstanceFactoryTest {
  private NewInstanceFactory factory;
  private JCodeModel codeModel;
  private JFormatter formatter;
  private StringWriter out;

  @BeforeMethod
  protected void setUp() throws Exception {
    codeModel = new JCodeModel();
    factory = new NewInstanceFactory( codeModel, new ClassRefSupport( codeModel ) );
    initializeFormatter();
  }

  private void initializeFormatter() {
    out = new StringWriter();
    formatter = new JFormatter( new PrintWriter( out ) );
  }

  @Test
  public void testString() throws IOException {
    assertFactory( String.class, "\"daValue\"" );
  }

  @Test
  public void testPrim() throws IOException {
    assertFactory( Integer.TYPE, "42" );
    assertFactory( Double.TYPE, "12.5D" );
    assertFactory( Float.TYPE, "44.0F" );
    assertFactory( Long.TYPE, "43L" );
    assertFactory( Boolean.TYPE, "true" );
    assertFactory( Character.TYPE, "'c'" );
  }

  @Test
  public void testNum() throws IOException {
    assertFactory( Integer.class, "java.lang.Integer.valueOf(42)" );
    assertFactory( Double.class, "java.lang.Double.valueOf(12.5D)" );
    assertFactory( Float.class, "java.lang.Float.valueOf(44.0F)" );
    assertFactory( Long.class, "java.lang.Long.valueOf(43L)" );
    assertFactory( Boolean.class, "java.lang.Boolean.TRUE" );
  }

  @Test
  public void testObject() throws IOException {
    assertFactory( Object.class, "new java.lang.Object()" );
  }

  private void assertFactory( @NotNull Class<?> type, @NotNull @NonNls String expected ) throws IOException {
    initializeFormatter();
    factory.create( new TypeMirrorMock( type ), "daValue" ).generate( formatter );
    assertEquals( out.toString().trim(), expected.trim() );
  }
}
