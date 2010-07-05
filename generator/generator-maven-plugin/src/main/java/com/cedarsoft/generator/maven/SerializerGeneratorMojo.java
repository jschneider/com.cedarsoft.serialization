package com.cedarsoft.generator.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal generate
 */
public class SerializerGeneratorMojo extends AbstractMojo {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info( "Executing Generator" );
  }
}
