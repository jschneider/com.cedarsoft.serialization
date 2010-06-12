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

package com.cedarsoft.serialization.generator.model;

import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import org.testng.annotations.*;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.*;

/**
 *
 */
public class ParsingFieldInitTypesTest {
  private DomainObjectDescriptorFactory factory;

  @BeforeMethod
  protected void setUp() throws Exception {
    URL resource = getClass().getResource( "/com/cedarsoft/serialization/generator/parsing/test/FieldTypesInit.java" );
    assertNotNull( resource );
    File javaFile = new File( resource.toURI() );
    assertTrue( javaFile.exists() );

    Result parsed = Parser.parse( javaFile );
    assertNotNull( parsed );
    assertEquals( parsed.getClassDeclarations().size(), 1 );
    ClassDeclaration classDeclaration = parsed.getClassDeclaration( "com.cedarsoft.serialization.generator.parsing.test.FieldTypesInit" );

    factory = new DomainObjectDescriptorFactory( classDeclaration );
  }

  @Test
  public void testFindField() {
    FieldDeclaration fieldDeclaration = factory.findFieldDeclaration( "width" );
    assertEquals( fieldDeclaration.getSimpleName(), "width" );
    assertEquals( fieldDeclaration.getType().toString(), "double" );
  }

  @Test
  public void testFindSetterInit() {
    FieldDeclaration fieldDeclaration = factory.findFieldDeclaration( "height" );
    MethodDeclaration setter = factory.findSetter( fieldDeclaration );
    assertNotNull( setter );
    assertEquals( setter.getSimpleName(), "setHeight" );
  }

  @Test
  public void testInitTypes() {
    DomainObjectDescriptor descriptor = factory.create();
    assertEquals( descriptor.getFieldsInitializedInConstructor().size(), 2 );
    assertEquals( descriptor.getFieldsInitializedInConstructor().get( 0 ).getSimpleName(), "description" );
    assertEquals( descriptor.getFieldsInitializedInConstructor().get( 1 ).getSimpleName(), "width" );

    assertEquals( descriptor.getFieldsInitializedInSetter().size(), 1 );
    assertEquals( descriptor.getFieldsInitializedInSetter().get( 0 ).getSimpleName(), "height" );
  }
}
