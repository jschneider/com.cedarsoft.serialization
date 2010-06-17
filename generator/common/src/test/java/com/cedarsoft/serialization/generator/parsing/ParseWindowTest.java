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

package com.cedarsoft.serialization.generator.parsing;

import com.cedarsoft.codegen.parser.Parser;
import com.cedarsoft.codegen.parser.Result;
import com.google.common.collect.ImmutableList;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import org.testng.annotations.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.*;

/**
 *
 */
public class ParseWindowTest {
  private File javaFile;

  @BeforeMethod
  protected void setUp() throws Exception {
    URL resource = getClass().getResource( "/com/cedarsoft/serialization/generator/parsing/test/Window.java" );
    assertNotNull( resource );
    javaFile = new File( resource.toURI() );
    assertTrue( javaFile.exists() );
  }

  @Test
  public void testParsing() throws ClassNotFoundException, NoSuchMethodException {
    Result parsed = Parser.parse( javaFile );
    assertNotNull( parsed );

    assertEquals( parsed.getClassDeclarations().size(), 1 );

    ClassDeclaration classDeclaration = parsed.getClassDeclaration( "com.cedarsoft.serialization.generator.parsing.test.Window" );
    {
      assertEquals( classDeclaration.getConstructors().size(), 1 );
      ConstructorDeclaration constructor = classDeclaration.getConstructors().iterator().next();
      assertEquals( constructor.getParameters().size(), 3 );

      assertEquals( constructor.getDocComment(), " the constructor\n" +
        "\n" +
        " @param description the descri\n" +
        " @param width       the width\n" +
        " @param height      the height\n" );

      List<ParameterDeclaration> params = ImmutableList.copyOf( constructor.getParameters() );

      assertEquals( params.get( 0 ).getType().toString(), "java.lang.String" );
      assertEquals( params.get( 0 ).getSimpleName(), "description" );
      assertEquals( params.get( 0 ).getDocComment(), null );
      assertEquals( params.get( 0 ).getPosition().line(), 53 );
      assertEquals( params.get( 0 ).getPosition().column(), 25 );

      assertEquals( params.get( 1 ).getType().toString(), "double" );
      assertEquals( params.get( 1 ).getSimpleName(), "width" );
      assertEquals( params.get( 2 ).getType().toString(), "double" );
      assertEquals( params.get( 2 ).getSimpleName(), "height" );
    }

    {
      List<FieldDeclaration> fields = ImmutableList.copyOf( classDeclaration.getFields() );
      assertEquals( fields.size(), 3 );
      assertEquals( fields.get( 0 ).getType().toString(), "double" );
      assertEquals( fields.get( 1 ).getType().toString(), "double" );
      assertEquals( fields.get( 2 ).getType().toString(), "java.lang.String" );

      assertEquals( fields.get( 0 ).getSimpleName(), "width" );
      assertEquals( fields.get( 1 ).getSimpleName(), "height" );
      assertEquals( fields.get( 2 ).getSimpleName(), "description" );

      assertEquals( fields.get( 0 ).getDocComment().trim(), "the comment for field width" );
      assertEquals( fields.get( 0 ).getModifiers().toString(), "[private, final]" );
    }

    {
      ImmutableList<MethodDeclaration> methods = ImmutableList.copyOf( classDeclaration.getMethods() );
      assertEquals( methods.size(), 5 );

      assertEquals( methods.get( 0 ).getSimpleName(), "getWidth" );
      assertEquals( methods.get( 1 ).getSimpleName(), "getHeight" );
      assertEquals( methods.get( 2 ).getSimpleName(), "getDescription" );
      assertEquals( methods.get( 3 ).getSimpleName(), "equals" );
      assertEquals( methods.get( 4 ).getSimpleName(), "hashCode" );


      assertEquals( methods.get( 0 ).getModifiers().toString(), "[public]" );
      assertEquals( methods.get( 0 ).getReturnType().toString(), "double" );
      assertEquals( methods.get( 0 ).getThrownTypes().size(), 0 );
      assertEquals( methods.get( 0 ).getFormalTypeParameters().size(), 0 );
      assertEquals( methods.get( 0 ).getParameters().size(), 0 );

      assertEquals( methods.get( 3 ).getModifiers().toString(), "[public]" );
      assertEquals( methods.get( 3 ).getReturnType().toString(), "boolean" );
      assertEquals( methods.get( 3 ).getThrownTypes().size(), 0 );
      assertEquals( methods.get( 3 ).getFormalTypeParameters().size(), 0 );
      assertEquals( methods.get( 3 ).getParameters().size(), 1 );
      List<ParameterDeclaration> params = ImmutableList.copyOf( methods.get( 3 ).getParameters() );
      assertEquals( params.size(), 1 );
      assertEquals( params.get( 0 ).getType().toString(), "java.lang.Object" );
      assertEquals( params.get( 0 ).getSimpleName(), "o" );
    }
  }
}
