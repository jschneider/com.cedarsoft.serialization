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

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptorFactory;
import com.cedarsoft.serialization.generator.model.FieldInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.staxmate.serializer.StaxMateGenerator;
import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.api.util.APTClassLoader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 */
public class Generator {
  @NonNls
  public static final String HELP_OPTION = "h";
  @NonNls
  public static final String VERSION_OPTION = "v";
  @NonNls
  public static final String OPTION_DESTINATION = "d";
  @NonNls
  public static final String OPTION_TEST_DESTINATION = "t";

  public static void main( @NotNull @NonNls String[] args ) throws Exception {
    Options options = buildOptions();
    CommandLine commandLine;
    try {
      commandLine = new GnuParser().parse( options, args );
    } catch ( MissingOptionException e ) {
      printError( options, e.getMessage() );
      return;
    }

    if ( commandLine.hasOption( HELP_OPTION ) ) {
      printHelp( options );
      return;
    }
    if ( commandLine.hasOption( VERSION_OPTION ) ) {
      printVersion();
    }

    List<? extends String> domainObjectNames = commandLine.getArgList();
    if ( domainObjectNames.size() != 1 ) {
      printError( options, "Missing class" );
      return;
    }

    File domainSourceFile = new File( domainObjectNames.get( 0 ) );
    if ( !domainSourceFile.isFile() ) {
      printError( options, "No source file found at <" + domainSourceFile.getAbsolutePath() + ">" );
    }
    File destination = new File( commandLine.getOptionValue( OPTION_DESTINATION ) );
    if ( !destination.isDirectory() ) {
      printError( options, "Destination <" + destination.getAbsolutePath() + "> is not a directory" );
    }

    File testDestination = new File( commandLine.getOptionValue( OPTION_TEST_DESTINATION ) );
    if ( !testDestination.isDirectory() ) {
      printError( options, "Test destination <" + testDestination.getAbsolutePath() + "> is not a directory" );
    }

    GeneratorConfiguration configuration = new GeneratorConfiguration( domainSourceFile, destination, testDestination );

    System.out.println( "Generating serializer for <" + domainSourceFile.getAbsolutePath() + ">" );
    System.out.println( "\tSerializer is created in <" + destination.getAbsolutePath() + ">" );
    System.out.println( "\tSerializer tests are created in <" + testDestination.getAbsolutePath() + ">" );


    //Now start the generator
    ClassLoader defaultClassLoader = Generator.class.getClassLoader();
    if ( defaultClassLoader == null ) {
      defaultClassLoader = ClassLoader.getSystemClassLoader();
    }

    ClassLoader aptClassLoader = new APTClassLoader( defaultClassLoader, PACKAGE_PREFIXES );
    Thread.currentThread().setContextClassLoader( aptClassLoader );

    Class<?> runnerType = aptClassLoader.loadClass( "com.cedarsoft.serialization.generator.Generator$GeneratorRunner" );

    Method method = runnerType.getMethod( "generate", GeneratorConfiguration.class );
    method.invoke( null, configuration );
  }

  private static void printError( Options options, String errorMessage ) {
    System.out.println( errorMessage );
    printHelp( options );
  }

  private static void printVersion() {
    System.out.println( Messages.VERSION.format() );
  }

  private static void printHelp( @NotNull Options options ) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "gen-ser -d <serializer dest dir> -t <test dest dir> path-to-class", options );
  }

  @NotNull
  private static Options buildOptions() {
    Options options = new Options();
    {
      Option option = new Option( OPTION_DESTINATION, "destination", true, "the output directory for the created serializers" );
      option.setRequired( true );
      options.addOption( option );
    }

    {
      Option option = new Option( OPTION_TEST_DESTINATION, "test-destination", true, "the output directory for the created serializer tests" );
      option.setRequired( true );
      options.addOption( option );
    }
    options.addOption( VERSION_OPTION, "version", false, "display version information" );
    options.addOption( HELP_OPTION, "help", false, "display this use message" );

    return options;
  }

  private static void printUsage() {
    System.out.println( Messages.USAGE.format() );
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
    public static void generate( @NotNull GeneratorConfiguration configuration ) throws IOException, JClassAlreadyExistsException {
      Result result = Parser.parse( configuration.getDomainSourceFile() );

      DomainObjectDescriptor descriptor = new DomainObjectDescriptorFactory( result.getClassDeclaration(), result.getEnvironment().getTypeUtils() ).create();

      XmlDecisionCallback decisionCallback = new XmlDecisionCallback() {
        @NotNull
        @Override
        public Target getSerializationTarget( @NotNull FieldInfo fieldInfo ) {
          return Target.ELEMENT;
        }
      };

      System.out.println( "Generating Serializer" );
      CodeGenerator<XmlDecisionCallback> serializerCodeGenerator = new CodeGenerator<XmlDecisionCallback>( decisionCallback );

      JDefinedClass serializerClass = new StaxMateGenerator( serializerCodeGenerator ).generateSerializer( descriptor );
      serializerCodeGenerator.getModel().build( configuration.getDestination(), System.out );

      CodeGenerator<XmlDecisionCallback> testCodeGenerator = new CodeGenerator<XmlDecisionCallback>( decisionCallback );

      System.out.println( "Generating Serializer Tests" );
      new com.cedarsoft.serialization.generator.output.staxmate.serializer.test.StaxMateGenerator( testCodeGenerator ).generateSerializerTest( serializerClass.fullName(), descriptor );
      new com.cedarsoft.serialization.generator.output.staxmate.serializer.test.StaxMateGenerator( testCodeGenerator ).generateSerializerVersionTest( serializerClass, descriptor );

      testCodeGenerator.getModel().build( configuration.getTestDestination(), System.out );
    }
  }
}
