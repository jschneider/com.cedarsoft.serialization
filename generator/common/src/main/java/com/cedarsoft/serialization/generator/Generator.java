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

package com.cedarsoft.serialization.generator;

import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.TypeMirror;
import com.sun.tools.xjc.api.util.APTClassLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 *
 */
public class Generator {
  public static void main( String[] args ) throws Exception {
    System.out.println( "Starting runner..." );
    File file = new File( "/home/johannes/projects/com.cedarsoft.serialization/generator/common/src/test/resources/com/cedarsoft/serialization/codemodel/JavaClassToParse.java" );

    ClassLoader defaultClassLoader = Generator.class.getClassLoader();
    if ( defaultClassLoader == null ) {
      defaultClassLoader = ClassLoader.getSystemClassLoader();
    }

    ClassLoader aptClassLoader = new APTClassLoader( defaultClassLoader, PACKAGE_PREFIXES );
    Thread.currentThread().setContextClassLoader( aptClassLoader );

    Class<?> runnerType = aptClassLoader.loadClass( "com.cedarsoft.serialization.generator.Generator$GeneratorRunner" );

    Method method = runnerType.getMethod( "generate", File.class );
    method.invoke( null, file );
  }

  @NotNull
  @NonNls
  private static final String[] PACKAGE_PREFIXES = {
    "com.cedarsoft.serialization.",
    //    "com.sun.tools.jxc.",
    //    "com.sun.tools.xjc.",
    "com.sun.istack.tools.",
    "com.sun.tools.apt.",
    "com.sun.tools.javac.",
    "com.sun.tools.javadoc.",
    "com.sun.mirror."
  };

  /**
   * Static inner class that is necessary due to ClassLoader issues.
   * We want to be sure that everything works in the context off the APTClassLoader
   */
  public static class GeneratorRunner {
    public static void generate( @NotNull File file ) throws IOException, JClassAlreadyExistsException {
      Result result = Parser.parse( file );

      System.out.println( "parsing finished successfully" );
      for ( ClassDeclaration classDeclaration : result.getClassDeclarations() ) {
        System.out.println( "\t" + classDeclaration.getQualifiedName() );
      }

      System.out.println( "---------" );

      generateCode( result.getClassDeclarations().get( 1 ) );
    }

    private static void generateCode( @NotNull ClassDeclaration classDeclaration ) throws JClassAlreadyExistsException, IOException {
      CodeWriter codeWriter = new SingleStreamCodeWriter( System.out );

      String name = classDeclaration.getQualifiedName();

      JCodeModel codeModel = new JCodeModel();
      {
        JDefinedClass fooClass = codeModel._class( name );

        //SuperClass
        ClassType superclass = classDeclaration.getSuperclass();
        if ( superclass != null ) {
          JClass theClass = codeModel.directClass( superclass.getDeclaration().getQualifiedName() );

          if ( superclass.getActualTypeArguments().size() == 1 ) {
            TypeMirror mirror = superclass.getActualTypeArguments().iterator().next();
            JClass aClass = codeModel.directClass( mirror.toString() );

            JClass narrowed = theClass.narrow( aClass.wildcard() );
            System.out.println( "narrwoed: " + narrowed );
          }
          

          fooClass._extends( codeModel.directClass( superclass.getDeclaration().getQualifiedName() ) );
        }

        //Interfaces
        for ( InterfaceType superInterface : classDeclaration.getSuperinterfaces() ) {
          fooClass._implements( codeModel.directClass( superInterface.getDeclaration().getQualifiedName() ) );
        }

        fooClass.field( JMod.PRIVATE, String.class, "id" );
      }

      codeModel.build( codeWriter );
    }
  }
}
