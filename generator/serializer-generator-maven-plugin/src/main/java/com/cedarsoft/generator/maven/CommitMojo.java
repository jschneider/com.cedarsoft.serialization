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

package com.cedarsoft.generator.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Commits the created serializers and tests to the source directory
 *
 * @goal commit
 */
public class CommitMojo extends AbstractGeneratorMojo {

  /**
   * The source directories containing the sources to be compiled.
   *
   * @parameter default-value="${project.compileSourceRoots}"
   * @required
   * @readonly
   */
  private List<String> sourceRoots;

  /**
   * The source directories containing the test sources to be compiled.
   *
   * @parameter default-value="${project.testCompileSourceRoots}"
   * @required
   * @readonly
   */
  private List<String> testSourceRoots;

  /**
   * Whether to commit the serializer
   *
   * @parameter expression="${commitSerializer}"
   * @readonly
   */
  protected boolean commitSerializer = true;
  /**
   * Whether to commit the tests
   *
   * @parameter expression="${commitTests}"
   * @readonly
   */
  protected boolean commitTests = true;

  /**
   * The maven session
   *
   * @parameter expression="${session}"
   * @required
   * @readonly
   */
  protected MavenSession context;

  /**
   * @component
   * @required
   * @readonly
   */
  private Prompter prompter;

  public void setPrompter( Prompter prompter ) {
    this.prompter = prompter;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if ( commitSerializer ) {
      try {
        commitSerializers();
      } catch ( IOException e ) {
        throw new MojoFailureException( "Commiting serializer failed due to " + e.getMessage(), e );
      }
    }

    if ( commitTests && !testOutputDirectory.exists() || testOutputDirectory.list().length == 0 ) {
      getLog().info( "No tests have been created in " + testOutputDirectory.getPath() );
      getLog().info( "Call serializer-generator:generate first." );
    }


    //    if ( testSourceRoots.isEmpty() ) {
    //      throw new MojoExecutionException( "No compile source roots available" );
    //    }
    //    String testSourceRootRelative = testSourceRoots.get( 0 );
    //    File testSourceRoot = new File( project.getBasedir(), testSourceRootRelative );
    //
    //
    //    getLog().info( "Committing serializers to " + sourceRoot.getPath() );
    //    getLog().info( "Committing serializer tests to " + testSourceRootRelative );
    //    getLog().info( "Committing serializer tests to " + testSourceRoot.getPath() );


    //    FileUtils.copyDirectory( outputDirectory, );


  }

  private void commitSerializers() throws MojoExecutionException, IOException {
    if ( !outputDirectory.exists() || outputDirectory.list().length == 0 ) {
      getLog().warn( "No serializers have been created in " + outputDirectory.getPath() );
      getLog().warn( "Call serializer-generator:generate first." );
      return;
    }

    MavenProject project = context.getCurrentProject();

    if ( sourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No compile source roots available" );
    }

    File sourceRoot = new File( sourceRoots.get( 0 ) );
    getLog().info( "Committing serializers to " + sourceRoot.getPath() );

    List<? extends String> serializerFileNames = FileUtils.getFileNames( outputDirectory, "**", null, true );
    for ( String serializerFileName : serializerFileNames ) {
      String relative = serializerFileName.substring( outputDirectory.getPath().length() + 1 );

      getLog().info( "Committing: " + relative );

      File target = new File( sourceRoot, relative );
      if ( target.exists() ) {
        try {
          @NonNls
          String answer = prompter.prompt( "Serializer " + relative + " still exists. Really overwrite?", "no" );
          getLog().info( "Answer: " + answer );

          if ( !answer.equalsIgnoreCase( "yes" ) ) {
            throw new MojoExecutionException( "Cannot commit serializer: Still exists at " + target.getPath() );
          }
        } catch ( PrompterException e ) {
          throw new MojoExecutionException( "Cannot commit serializer: Still exists at " + target.getPath(), e );
        }
      }
      File source = new File( outputDirectory, relative );

      getLog().info( "Moving serializer" );
      getLog().info( "\tfrom: " + source.getPath() );
      getLog().info( "\tto: " + target.getPath() );

      FileUtils.copyFile( source, target );
    }
  }
}
