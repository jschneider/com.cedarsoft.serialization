package com.cedarsoft.generator.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 *
 */
public abstract class AbstractCommitMojo extends AbstractGeneratorMojo {


  /**
   * The source directories containing the sources to be compiled.
   *
   * @parameter default-value="${project.compileSourceRoots}"
   * @required
   * @readonly
   */
  protected List<String> sourceRoots;
  /**
   * The source directories containing the test sources to be compiled.
   *
   * @parameter default-value="${project.testCompileSourceRoots}"
   * @required
   * @readonly
   */
  protected List<String> testSourceRoots;
  /**
   * @component
   * @required
   * @readonly
   */
  protected Prompter prompter;

  @NotNull
  protected File getTestSourceRoot() throws MojoExecutionException {
    if ( testSourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No test compile source roots available" );
    }
    File testSourceRoot = new File( testSourceRoots.get( 0 ) );
    return testSourceRoot;
  }

  @NotNull
  protected File getSourceRoot() throws MojoExecutionException {
    if ( sourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No compile source roots available" );
    }
    File sourceRoot = new File( sourceRoots.get( 0 ) );
    return sourceRoot;
  }
}
