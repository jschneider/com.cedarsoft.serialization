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
import com.cedarsoft.serialization.NotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation that is based on simple files.
 */
public class FileBasedObjectsAccess implements StreamBasedObjectsAccess {
  @Nonnull

  private final File baseDir;

  @Nonnull

  private final String extension;

  public FileBasedObjectsAccess( @Nonnull File baseDir, @Nonnull String extension ) {
    assert baseDir.exists();
    assert baseDir.isDirectory();

    this.baseDir = baseDir;
    this.extension = extension;
  }

  @Nonnull
  @Override
  public Set<? extends String> getIds() throws IOException {
    assert baseDir.exists();
    File[] files = baseDir.listFiles( ( FileFilter ) new SuffixFileFilter( extension ) );
    if ( files == null ) {
      throw new FileNotFoundException( "Could not list files in " + baseDir.getAbsolutePath() );
    }

    Set<String> ids = new HashSet<String>();
    for ( File file : files ) {
      ids.add( FilenameUtils.getBaseName( file.getName() ) );
    }

    return ids;
  }

  @Override
  @Nonnull
  public OutputStream openOut( @Nonnull String id ) throws FileNotFoundException {
    File file = getFile( id );
    if ( file.exists() ) {
      throw new StillContainedException( id );
    }
    return new BufferedOutputStream( new FileOutputStream( file ) );
  }

  @Override
  public OutputStream openOutForUpdate( @Nonnull String id ) throws NotFoundException, FileNotFoundException {
    File file = getFile( id );
    if ( !file.exists() ) {
      throw new NotFoundException( id );
    }
    return new BufferedOutputStream( new FileOutputStream( file ) );
  }

  @Override
  @Nonnull
  public InputStream getInputStream( @Nonnull String id ) throws FileNotFoundException {
    return new BufferedInputStream( new FileInputStream( getFile( id ) ) );
  }

  @Override
  public void delete( @Nonnull String id ) throws NotFoundException {
    File file = getFile( id );
    if ( !file.exists() ) {
      throw new NotFoundException( "No entry found for <" + id + ">" );
    }

    file.delete();
  }

  @Nonnull
  private File getFile( @Nonnull String id ) {
    return new File( baseDir, id + '.' + extension );
  }

  @Nonnull
  public File getBaseDir() {
    return baseDir;
  }
}
