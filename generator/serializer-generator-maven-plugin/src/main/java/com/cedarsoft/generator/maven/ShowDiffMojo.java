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

import com.cedarsoft.exec.Executer;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Shows a diff between the generated sources and the regular classes.
 *
 * @goal diff
 */
public class ShowDiffMojo extends SourceFolderAwareMojo {
  /**
   * The diff command that shall be executed.
   * The default value is "diff {0} {1}"
   *
   * @parameter expression="${diffCommand}"
   */
  @NonNls
  private String diffCommand = "diff {0} {1}";

  @NonNls
  protected String getDiffCommand() {
    return diffCommand;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info( "Showing diff" );

    Collection<String> skippedFiles = new ArrayList<String>();
    try {
      {
        try {
          skippedFiles.addAll( showDiffs( getOutputDirectory(), getSourceRoot() ) );
          skippedFiles.addAll( showDiffs( getResourcesOutputDirectory(), getResourcesRoot() ) );
        } catch ( IOException e ) {
          throw new MojoFailureException( "Diff failed due to " + e.getMessage(), e );
        }
      }

      {
        try {
          skippedFiles.addAll( showDiffs( getTestOutputDirectory(), getTestSourceRoot() ) );
          skippedFiles.addAll( showDiffs( getTestResourcesOutputDirectory(), getTestResourcesRoot() ) );
        } catch ( IOException e ) {
          throw new MojoFailureException( "Diff failed due to " + e.getMessage(), e );
        }
      }

      if ( !skippedFiles.isEmpty() ) {
        getLog().info( "" );
        getLog().info( "-----------------------------------------------" );
        getLog().info( "Those files do *not* exist in the source folder:" );
        getLog().info( "" );

        for ( String skipped : skippedFiles ) {
          getLog().info( "\t- " + skipped );
        }
        getLog().info( "-----------------------------------------------" );
      }

    } catch ( InterruptedException e ) {
      throw new MojoExecutionException( "Failure", e );
    }
  }

  @NotNull
  protected List<String> showDiffs( @NotNull File sourceDirectory, @NotNull File targetDir ) throws MojoExecutionException, IOException, InterruptedException {
    if ( !sourceDirectory.exists() || sourceDirectory.list().length == 0 ) {
      getLog().warn( "No generated files found in " + sourceDirectory.getPath() );
      getLog().warn( "Call the <generate> goal first." );
      return Collections.emptyList();
    }

    getLog().info( "Showing diff for generated files comparing to " + targetDir.getPath() );

    List<String> skippedFiles = new ArrayList<String>();

    List<? extends String> filesNames = FileUtils.getFileNames( sourceDirectory, "**", null, true );
    for ( String serializerFileName : filesNames ) {
      String relative = serializerFileName.substring( sourceDirectory.getPath().length() + 1 );

      File generated = new File( targetDir, relative );
      if ( generated.exists() ) {
        getLog().info( "Showing diff for: " + relative );
        File inSourceDir = new File( sourceDirectory, relative );

        showDiff( inSourceDir, generated );
      } else {
        skippedFiles.add( relative );
        getLog().info( "No corresponding file found for " + relative );
      }
    }

    return skippedFiles;
  }

  private void showDiff( @NotNull File src, @NotNull File generated ) throws IOException, InterruptedException {
    String commandLine = buildCommandLine( src.getAbsolutePath(), generated.getAbsolutePath() );
    getLog().info( "Executing <" + commandLine + ">" );
    Executer executer = new Executer( new ProcessBuilder( Lists.newArrayList( Splitter.on( " " ).split( commandLine ) ) ), true );
    executer.execute();
  }

  @NotNull
  @NonNls
  public String buildCommandLine( @NotNull @NonNls String firstPath, @NotNull @NonNls String secondPath ) {
    return MessageFormat.format( getDiffCommand(), firstPath, secondPath );
  }
}
