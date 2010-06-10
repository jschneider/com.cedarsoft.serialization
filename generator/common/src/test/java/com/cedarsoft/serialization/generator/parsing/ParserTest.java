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

import com.google.common.collect.ImmutableList;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeParameterDeclaration;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.TypeMirror;
import org.testng.annotations.*;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

/**
 *
 */
public class ParserTest {
  private File javaFile;
  private Result parsed;

  @BeforeMethod
  protected void setUp() throws Exception {
    URL resource = getClass().getResource( "/com/cedarsoft/serialization/generator/parsing/test/JavaClassToParse.java" );
    assertNotNull( resource );
    javaFile = new File( resource.toURI() );
    assertTrue( javaFile.exists() );
    parsed = Parser.parse( javaFile );
    assertNotNull( parsed );
  }

  @Test
  public void testWildcards() {
    ClassDeclaration classDeclaration = parsed.getClassDeclaration( "com.cedarsoft.serialization.generator.parsing.test.JavaClassToParse.InnerStaticClass" );

    ImmutableList<FieldDeclaration> fields = ImmutableList.copyOf( classDeclaration.getFields() );
    FieldDeclaration field = fields.get( 0 );
    assertEquals( field.getSimpleName(), "stringList" );

    TypeMirror type = field.getType();
    assertEquals( type.toString(), "java.util.List<java.lang.String>" );
    assertEquals( type.getClass().getName(), "com.sun.tools.apt.mirror.type.InterfaceTypeImpl" );

    {
      InterfaceType interfaceType = ( InterfaceType ) type;
      assertEquals( interfaceType.getSuperinterfaces().size(), 1 );
      assertEquals( interfaceType.getSuperinterfaces().iterator().next().toString(), "java.util.Collection<java.lang.String>" );

      //Checking the declaration (the java.util.List class itself!
      {
        assertEquals( interfaceType.getDeclaration().toString(), "java.util.List<E>" );
        assertEquals( interfaceType.getDeclaration().getQualifiedName(), "java.util.List" );
        assertEquals( interfaceType.getDeclaration().getSimpleName(), "List" );
        assertEquals( interfaceType.getDeclaration().getPackage().getQualifiedName(), "java.util" );
        assertEquals( interfaceType.getDeclaration().getFormalTypeParameters().size(), 1 );
        TypeParameterDeclaration typeParameter = interfaceType.getDeclaration().getFormalTypeParameters().iterator().next();
        assertEquals( typeParameter.getSimpleName(), "E" );
        assertEquals( typeParameter.getOwner(), interfaceType.getDeclaration() );
        assertEquals( typeParameter.getOwner().toString(), "java.util.List<E>" );
        assertEquals( typeParameter.getBounds().size(), 1 );
        assertEquals( typeParameter.getBounds().iterator().next().toString(), "java.lang.Object" );
      }

      //Check the interface itself
      List<TypeMirror> actualTypeArgs = ImmutableList.copyOf( interfaceType.getActualTypeArguments() );
      assertEquals( actualTypeArgs.size(), 1 );
      assertEquals( actualTypeArgs.get( 0 ).toString(), "java.lang.String" );
      assertEquals( actualTypeArgs.get( 0 ).getClass().getName(), "com.sun.tools.apt.mirror.type.ClassTypeImpl" );
      assertEquals( ( ( ClassType ) actualTypeArgs.get( 0 ) ).getDeclaration().getSimpleName(), "String" );
      assertEquals( ( ( ClassType ) actualTypeArgs.get( 0 ) ).getSuperclass().toString(), "java.lang.Object" );
      assertEquals( ( ( DeclaredType ) actualTypeArgs.get( 0 ) ).getDeclaration().getMethods().size(), 69 );
    }
  }

  @Test
  public void testParsing() throws ClassNotFoundException, NoSuchMethodException {
    assertEquals( parsed.getClassDeclarations().size(), 4 );

    ClassDeclaration classDeclaration = parsed.getClassDeclaration( "com.cedarsoft.serialization.generator.parsing.test.JavaClassToParse.InnerStaticClass" );
    {
      assertEquals( classDeclaration.getConstructors().size(), 1 );
      assertEquals( classDeclaration.getConstructors().iterator().next().getParameters().size(), 1 );
      ParameterDeclaration parameterDeclaration = classDeclaration.getConstructors().iterator().next().getParameters().iterator().next();
      assertEquals( parameterDeclaration.getType().toString(), "int" );
    }

    {
      assertEquals( classDeclaration.getMethods().size(), 4 );
      Iterator<MethodDeclaration> methodIter = classDeclaration.getMethods().iterator();
      MethodDeclaration method0 = methodIter.next();
      MethodDeclaration method1 = methodIter.next();
      MethodDeclaration method2 = methodIter.next();
      MethodDeclaration method3 = methodIter.next();

      assertEquals( method0.getSimpleName(), "getStringList" );
      assertEquals( method1.getSimpleName(), "getWildStringList" );
      assertEquals( method2.getSimpleName(), "doIt" );
      assertEquals( method3.getSimpleName(), "compareTo" );
    }
  }
}
