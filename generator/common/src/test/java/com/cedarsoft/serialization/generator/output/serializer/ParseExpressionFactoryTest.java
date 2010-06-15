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

import com.cedarsoft.serialization.generator.model.DefaultFieldTypeInformation;
import com.cedarsoft.serialization.generator.output.ClassRefSupport;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFormatter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.StringWriter;

import static org.testng.Assert.*;

/**
 *
 */
public class ParseExpressionFactoryTest {
  private JCodeModel model;
  private ParseExpressionFactory factory;

  @BeforeMethod
  protected void setUp() throws Exception {
    model = new JCodeModel();
    factory = new ParseExpressionFactory( model, new ClassRefSupport( model ) );
  }

  @Test
  public void testString() {
    checkForType( String.class, "aCall()" );
  }

  @Test
  public void testNumbers() {
    checkForType( Double.class, "java.lang.Double.parseDouble(aCall())" );
    checkForType( Double.TYPE, "java.lang.Double.parseDouble(aCall())" );
    checkForType( Integer.class, "java.lang.Integer.parseInt(aCall())" );
    checkForType( Integer.TYPE, "java.lang.Integer.parseInt(aCall())" );
    checkForType( Float.class, "java.lang.Float.parseFloat(aCall())" );
    checkForType( Float.TYPE, "java.lang.Float.parseFloat(aCall())" );
    checkForType( Boolean.class, "java.lang.Boolean.parseBoolean(aCall())" );
    checkForType( Boolean.TYPE, "java.lang.Boolean.parseBoolean(aCall())" );
  }

  private void checkForType( @NotNull Class<?> type, @NotNull @NonNls String expected ) {
    JExpression parseExpression = factory.createParseExpression( JExpr.invoke( "aCall" ), new DefaultFieldTypeInformation( new TypeMirrorMock( type ) ) );

    StringWriter out = new StringWriter();
    parseExpression.generate( new JFormatter( out ) );
    assertEquals( out.toString(), expected );
  }

}
