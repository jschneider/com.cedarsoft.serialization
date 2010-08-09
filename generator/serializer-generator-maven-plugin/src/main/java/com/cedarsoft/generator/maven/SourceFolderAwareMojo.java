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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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

  protected Prompter getPrompter() {
    return prompter;
  }

  @NotNull
  protected File getTestSourceRoot() throws MojoExecutionException {
    if ( testSourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No test compile source roots available" );
    }
    return new File( testSourceRoots.get( 0 ) );
  }

  @NotNull
  protected File getSourceRoot() throws MojoExecutionException {
    if ( sourceRoots.isEmpty() ) {
      throw new MojoExecutionException( "No compile source roots available" );
    }
    return new File( sourceRoots.get( 0 ) );
  }

  @NotNull
  protected File getResourcesRoot() throws MojoExecutionException {
    if ( resources.isEmpty() ) {
      throw new MojoExecutionException( "No resource roots available" );
    }
    return new File( resources.get( 0 ).getDirectory() );
  }

  @NotNull
  protected File getTestResourcesRoot() throws MojoExecutionException {
    if ( testResources.isEmpty() ) {
      throw new MojoExecutionException( "No test resource roots available" );
    }
    return new File( testResources.get( 0 ).getDirectory() );
  }
}
