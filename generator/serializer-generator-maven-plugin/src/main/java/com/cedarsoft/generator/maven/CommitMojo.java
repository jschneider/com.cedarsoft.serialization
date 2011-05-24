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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.FileUtils;

import javax.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Commits the created classes and resources to the source directories
 *
 * @goal commit
 */
public class CommitMojo extends SourceFolderAwareMojo {
  /**
   * Whether to commit the serializer
   *
   * @parameter expression="${commitSerializers}"
   */
  protected boolean commitSerializers = true;
  /**
   * Whether to commit the tests
   *
   * @parameter expression="${commitTests}"
   */
  protected boolean commitTests = true;

  protected boolean commitTests() {
    return commitTests;
  }

  protected boolean commitSerializers() {
    return commitSerializers;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if ( commitSerializers() ) {
      try {
        commit( getOutputDirectory(), getSourceRoot() );
        commit( getResourcesOutputDirectory(), getResourcesRoot() );
      } catch ( IOException e ) {
        throw new MojoFailureException( "Committing production files/resources failed due to " + e.getMessage(), e );
      }
    }


    if ( commitTests() ) {
      try {
        commit( getTestOutputDirectory(), getTestSourceRoot() );
        commit( getTestResourcesOutputDirectory(), getTestResourcesRoot() );
      } catch ( IOException e ) {
        throw new MojoFailureException( "Committing test files/resources failed due to " + e.getMessage(), e );
      }
    }
  }

  protected void commit( @Nonnull File sourceDirectory, @Nonnull File targetDir ) throws MojoExecutionException, IOException {
    if ( !sourceDirectory.exists() || sourceDirectory.list().length == 0 ) {
      getLog().warn( "No generated files found in " + sourceDirectory.getPath() );
      getLog().warn( "Call <generate> goal first." );
      return;
    }

    getLog().info( "Committing generated files to " + targetDir.getPath() );

    List<? extends String> filesNames = FileUtils.getFileNames( sourceDirectory, "**", null, true );
    for ( String serializerFileName : filesNames ) {
      String relative = serializerFileName.substring( sourceDirectory.getPath().length() + 1 );

      getLog().info( "Committing: " + relative );

      File target = new File( targetDir, relative );
      if ( target.exists() ) {
        try {

          String answer = getPrompter().prompt( relative + " still exists. Really overwrite?", "no" );

          if ( answer.equalsIgnoreCase( "yes" ) ) {
            getLog().info( "Overwriting " + relative );
          } else {
            getLog().info( "Skipping " + relative );
            continue;
          }
        } catch ( PrompterException e ) {
          throw new MojoExecutionException( e.getMessage(), e );
        }
      }
      File source = new File( sourceDirectory, relative );

      getLog().info( "Moving generated file" );
      getLog().info( "\tfrom: " + source.getPath() );
      getLog().info( "\tto: " + target.getPath() );

      FileUtils.rename( source, target );
    }
  }
}
