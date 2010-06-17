package com.cedarsoft.serialization.generator;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptorFactory;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.serializer.AbstractGenerator;
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
import org.fest.reflect.core.Reflection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public abstract class Generator {
  @NonNls
  public static final String HELP_OPTION = "h";
  @NonNls
  public static final String VERSION_OPTION = "v";
  @NonNls
  public static final String OPTION_DESTINATION = "d";
  @NonNls
  public static final String OPTION_TEST_DESTINATION = "t";
  @NotNull
  @NonNls
  protected static final String[] PACKAGE_PREFIXES = {
    "com.cedarsoft.serialization.",
    "com.sun.istack.tools.",
    "com.sun.tools.apt.",
    "com.sun.tools.javac.",
    "com.sun.tools.javadoc.",
    "com.sun.mirror."
  };

  public void run( @NotNull @NonNls String[] args ) throws Exception {
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
    ClassLoader defaultClassLoader = getClass().getClassLoader();
    if ( defaultClassLoader == null ) {
      defaultClassLoader = ClassLoader.getSystemClassLoader();
    }

    ClassLoader aptClassLoader = new APTClassLoader( defaultClassLoader, PACKAGE_PREFIXES );
    Thread.currentThread().setContextClassLoader( aptClassLoader );

    Class<?> runnerType = aptClassLoader.loadClass( getRunnerClassName() );

    Object runner = Reflection.constructor().in( runnerType ).newInstance();
    Reflection.method( "generate" ).withParameterTypes( GeneratorConfiguration.class ).in( runner ).invoke( configuration );
  }

  @NotNull
  @NonNls
  protected abstract String getRunnerClassName();


  protected void printError( Options options, String errorMessage ) {
    System.out.println( errorMessage );
    printHelp( options );
  }

  protected void printVersion() {
    System.out.println( Messages.VERSION.format() );
  }

  protected void printHelp( @NotNull Options options ) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "gen-ser -d <serializer dest dir> -t <test dest dir> path-to-class", options );
  }

  @NotNull
  protected Options buildOptions() {
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

  /**
   * Static inner class that is necessary due to ClassLoader issues.
   * We want to be sure that everything works in the context off the APTClassLoader
   */
  public abstract static class AbstractGeneratorRunner<T extends DecisionCallback> {
    public void generate( @NotNull GeneratorConfiguration configuration ) throws IOException, JClassAlreadyExistsException {
      Result result = Parser.parse( configuration.getDomainSourceFile() );

      MirrorUtils.setTypes( result.getEnvironment().getTypeUtils() );
      DomainObjectDescriptor descriptor = new DomainObjectDescriptorFactory( result.getClassDeclaration() ).create();

      T decisionCallback = createDecisionCallback();

      System.out.println( "Generating Serializer" );
      CodeGenerator<T> serializerCodeGenerator = new CodeGenerator<T>( decisionCallback );

      JDefinedClass serializerClass = instantiateGenerator( serializerCodeGenerator ).generateSerializer( descriptor );
      serializerCodeGenerator.getModel().build( configuration.getDestination(), System.out );

      CodeGenerator<T> testCodeGenerator = new CodeGenerator<T>( decisionCallback );

      System.out.println( "Generating Serializer Tests" );
      instantiateTestGenerator( testCodeGenerator ).generateSerializerTest( serializerClass.fullName(), descriptor );
      instantiateTestGenerator( testCodeGenerator ).generateSerializerVersionTest( serializerClass, descriptor );

      testCodeGenerator.getModel().build( configuration.getTestDestination(), System.out );
    }

    @NotNull
    protected abstract T createDecisionCallback();

    @NotNull
    protected abstract com.cedarsoft.serialization.generator.output.serializer.test.AbstractGenerator<T> instantiateTestGenerator( @NotNull CodeGenerator<T> testCodeGenerator );

    @NotNull
    protected abstract AbstractGenerator<T> instantiateGenerator( @NotNull CodeGenerator<T> serializerCodeGenerator );
  }
}
