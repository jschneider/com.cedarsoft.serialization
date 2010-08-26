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

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.DecisionCallback;
import com.cedarsoft.codegen.GeneratorConfiguration;
import com.cedarsoft.codegen.model.DomainObjectDescriptor;
import com.cedarsoft.codegen.model.DomainObjectDescriptorFactory;
import com.cedarsoft.codegen.parser.Parser;
import com.cedarsoft.codegen.parser.Result;
import com.cedarsoft.io.WriterOutputStream;
import com.cedarsoft.serialization.generator.output.serializer.AbstractGenerator;
import com.google.common.collect.Lists;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.mirror.declaration.ClassDeclaration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 *
 */
public abstract class Generator extends com.cedarsoft.codegen.AbstractGenerator {
  @NotNull
  @Override
  protected List<? extends String> getPackagePrefixes() {
    List<String> list = Lists.newArrayList( super.getPackagePrefixes() );
    list.add( "com.cedarsoft.serialization." );
    return list;
  }

  /**
   * Static inner class that is necessary due to ClassLoader issues.
   * We want to be sure that everything works in the context off the APTClassLoader
   */
  public abstract static class AbstractGeneratorRunner<T extends DecisionCallback> implements Runner {
    @Override
    public void generate( @NotNull GeneratorConfiguration configuration ) throws IOException, JClassAlreadyExistsException {
      PrintStream statusPrinter = new PrintStream( new WriterOutputStream( configuration.getLogOut() ) );

      Result result = Parser.parse( null, configuration.getDomainSourceFiles() );

      if ( result.getClassDeclarations().isEmpty() ) {
        throw new IllegalStateException( "No class declarations found" );
      }

      for ( ClassDeclaration classDeclaration : result.getClassDeclarations() ) {
        DomainObjectDescriptor descriptor = new DomainObjectDescriptorFactory( classDeclaration ).create();
        generate( descriptor, configuration, statusPrinter );
      }
    }

    private void generate( @NotNull DomainObjectDescriptor descriptor, @NotNull GeneratorConfiguration configuration, @NotNull PrintStream statusPrinter ) throws JClassAlreadyExistsException, IOException {
      T decisionCallback = createDecisionCallback();

      //The Serializer
      if ( configuration.getCreationMode().isCreate() ) {
        configuration.getLogOut().println( "Generating Serializer:" );
        CodeGenerator<T> codeGenerator = new CodeGenerator<T>( decisionCallback );
        instantiateGenerator( codeGenerator ).generateSerializer( descriptor );
        codeGenerator.getModel().build( configuration.getDestination(), configuration.getResourcesDestination(), statusPrinter );
      }

      //The Serializer Tests
      if ( configuration.getCreationMode().isCreateTests() ) {
        CodeGenerator<T> testCodeGenerator = new CodeGenerator<T>( decisionCallback );

        configuration.getLogOut().println( "Generating Serializer Tests:" );
        String serializerClassName = AbstractGenerator.createSerializerClassName( descriptor.getQualifiedName() );
        instantiateTestGenerator( testCodeGenerator ).generateSerializerTest( serializerClassName, descriptor );
        instantiateTestGenerator( testCodeGenerator ).generateSerializerVersionTest( serializerClassName, descriptor );

        testCodeGenerator.getModel().build( configuration.getTestDestination(), configuration.getTestResourcesDestination(), statusPrinter );
      }
    }

    @NotNull
    protected abstract T createDecisionCallback();

    @NotNull
    protected abstract com.cedarsoft.serialization.generator.output.serializer.test.AbstractGenerator<T> instantiateTestGenerator( @NotNull CodeGenerator<T> testCodeGenerator );

    @NotNull
    protected abstract AbstractGenerator<T> instantiateGenerator( @NotNull CodeGenerator<T> serializerCodeGenerator );
  }
}
