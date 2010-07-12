package com.cedarsoft.generator.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 *
 */
public abstract class AbstractGeneratorMojo extends AbstractMojo {
  /**
   * Location of the output directory for the placeholder poms
   *
   * @parameter expression="${basedir}/target/generated-sources/cedarsoft-serialization-main"
   * @required
   * @readonly
   */
  protected File outputDirectory;
  /**
   * Location of the output directory for the placeholder poms
   *
   * @parameter expression="${basedir}/target/generated-sources/cedarsoft-serialization-test"
   * @required
   * @readonly
   */
  protected File testOutputDirectory;
  /**
   * Project artifacts.
   *
   * @parameter default-value="${project.artifact}"
   * @required
   * @readonly
   */
  protected Artifact projectArtifact;

  protected void prepareOutputDirectories() throws MojoExecutionException {
    verifyDirectories();
    outputDirectory.mkdirs();
    testOutputDirectory.mkdirs();
  }

  protected void verifyDirectories() throws MojoExecutionException {
    if ( outputDirectory == null ) {
      throw new MojoExecutionException( "output directory not set" );
    }
    if ( testOutputDirectory == null ) {
      throw new MojoExecutionException( "test output directory not set" );
    }
  }

  @NotNull
  public File getTestOutputDirectory() throws MojoExecutionException {
    if ( testOutputDirectory == null ) {
      throw new MojoExecutionException( "test output directory not set" );
    }
    return testOutputDirectory;
  }

  @NotNull
  public File getOutputDirectory() throws MojoExecutionException {
    if ( outputDirectory == null ) {
      throw new MojoExecutionException( "output directory not set" );
    }
    return outputDirectory;
  }
}
