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

package com.cedarsoft.serialization.serializers.stax.mate.registry;

import com.cedarsoft.exceptions.StillContainedException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation that is based on simple files.
 */
public class DirBasedObjectsAccess implements AbstractRegistrySerializingStrategy.ObjectsAccess {
  @Nonnull

  private final File baseDir;

  public DirBasedObjectsAccess( @Nonnull File baseDir ) {
    assert baseDir.exists();
    assert baseDir.isDirectory();

    this.baseDir = baseDir;
  }

  /**
   * Provides the ids
   *
   * @return the ids
   *
   * @throws FileNotFoundException
   */
  @Nonnull
  @Override
  public Set<? extends String> getIds() throws IOException {
    assert baseDir.exists();
    File[] dirs = baseDir.listFiles( ( FileFilter ) DirectoryFileFilter.DIRECTORY );
    if ( dirs == null ) {
      throw new FileNotFoundException( "Could not list dirs in " + baseDir.getAbsolutePath() );
    }

    Set<String> ids = new HashSet<String>();
    for ( File dir : dirs ) {
      ids.add( dir.getName() );
    }

    return ids;
  }

  @Nonnull
  public File addDirectory( @Nonnull String id ) throws StillContainedException {
    File dir = getDirInternal( id );
    if ( dir.exists() ) {
      throw new StillContainedException( id );
    }

    dir.mkdir();
    return dir;
  }

  @Nonnull
  public File getDirectory( @Nonnull String id ) throws FileNotFoundException {
    File directory = getDirInternal( id );
    if ( !directory.exists() ) {
      throw new FileNotFoundException( "No dir found for <" + id + "> at " + directory.getAbsolutePath() );
    }
    return directory;
  }

  @Nonnull
  private File getDirInternal( @Nonnull String id ) {
    return new File( baseDir, id );
  }

  @Nonnull
  public File getBaseDir() {
    return baseDir;
  }
}