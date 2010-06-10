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

import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import org.testng.annotations.*;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import static org.testng.Assert.*;

/**
 *
 */
public class ParserTest {
  private File javaFile;

  @BeforeMethod
  protected void setUp() throws Exception {
    URL resource = getClass().getResource( "/com/cedarsoft/serialization/generator/parsing/test/JavaClassToParse.java" );
    assertNotNull( resource );
    javaFile = new File( resource.toURI() );
    assertTrue( javaFile.exists() );
  }

  @Test
  public void testParsing() throws ClassNotFoundException, NoSuchMethodException {
    Result parsed = Parser.parse( javaFile );
    assertNotNull( parsed );

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
