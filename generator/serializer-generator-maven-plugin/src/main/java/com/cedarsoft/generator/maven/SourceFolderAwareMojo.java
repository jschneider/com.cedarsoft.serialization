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

import com.cedarsoft.codegen.parser.Classpath;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.components.interactivity.Prompter;
import javax.annotation.Nonnull;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public abstract class SourceFolderAwareMojo extends OutputFoldersAwareMojo {
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
   * The list of resources.
   *
   * @parameter default-value="${project.resources}"
   * @required
   * @readonly
   */
  private List<Resource> resources;

  /**
   * The list of test resources
   *
   * @parameter expression="${project.testResources}"
   * @required
   * @readonly
   */
  private List<Resource> testResources;


  /**
   * @component
   * @required
   * @readonly
   */
  protected Prompter prompter;


  @SuppressWarnings( "unchecked" )
  @Nonnull
  protected List<? extends String> getCompileClasspathElements() throws DependencyResolutionRequiredException {
    List elements = getProject().getCompileClasspathElements();
    if ( elements == null ) {
      return Collections.emptyList();
    }
    return elements;
  }

  @SuppressWarnings( "unchecked" )
  protected List<? extends String> getTestCompileClasspathElements() throws DependencyResolutionRequiredException {
    List elements = getProject().getTestClasspathElements();
    if ( elements == null ) {
      return Collections.emptyList();
    }
    return elements;
  }

  protected Prompter getPrompter() {
    return prompter;
  }

  @Nonnull
  protected File getTestSourceRoot() throws MojoExecutionException {
    if ( testSourceRoots == null || testSourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No test compile source roots available" );
    }
    return new File( testSourceRoots.get( 0 ) );
  }

  @Nonnull
  protected File getSourceRoot() throws MojoExecutionException {
    if ( sourceRoots == null || sourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No compile source roots available" );
    }
    return new File( sourceRoots.get( 0 ) );
  }

  @Nonnull
  protected File getResourcesRoot() throws MojoExecutionException {
    if ( resources == null || resources.isEmpty() ) {
      throw new MojoExecutionException( "No resource roots available" );
    }
    return new File( resources.get( 0 ).getDirectory() );
  }

  @Nonnull
  protected File getTestResourcesRoot() throws MojoExecutionException {
    if ( testResources == null || testResources.isEmpty() ) {
      throw new MojoExecutionException( "No test resource roots available" );
    }
    return new File( testResources.get( 0 ).getDirectory() );
  }

  @Nonnull
  protected Classpath buildClassPath() throws MojoExecutionException {
    Classpath classpath = new Classpath();

    try {
      for ( String classpathElement : getCompileClasspathElements() ) {
        File element = new File( classpathElement );
        getLog().debug( "Adding classpath element: " + element.getAbsolutePath() );
        classpath.add( element );
      }
    } catch ( DependencyResolutionRequiredException e ) {
      throw new MojoExecutionException( e.getMessage(), e );
    }

    return classpath;
  }

  @Nonnull
  protected Classpath buildTestClassPath() throws MojoExecutionException {
    Classpath classpath = new Classpath();

    try {
      for ( String classpathElement : getTestCompileClasspathElements() ) {
        File element = new File( classpathElement );
        getLog().debug( "Adding classpath element: " + element.getAbsolutePath() );
        classpath.add( element );
      }
    } catch ( DependencyResolutionRequiredException e ) {
      throw new MojoExecutionException( e.getMessage(), e );
    }

    return classpath;
  }
}
