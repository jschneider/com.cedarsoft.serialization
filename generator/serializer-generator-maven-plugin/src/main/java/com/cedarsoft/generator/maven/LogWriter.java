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

import com.google.common.base.Splitter;
import org.apache.maven.plugin.logging.Log;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 *
 */
public class LogWriter extends Writer {
  @NotNull
  @NonNls
  private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
  @NotNull
  @NonNls
  private final StringBuffer buffer = new StringBuffer();
  @NotNull
  private final Log log;

  LogWriter( @NotNull Log log ) {
    this.log = log;
  }

  @Override
  public void write( char[] cbuf, int off, int len ) throws IOException {
    buffer.append( cbuf, off, len );
  }

  @Override
  public void flush() throws IOException {
    Iterable<String> parts = Splitter.on( LINE_SEPARATOR ).split( buffer );
    for ( String part : parts ) {
      if ( part.length() > 0 ) {
        log.info( part );
      }
    }

    buffer.setLength( 0 );
  }

  @Override
  public void close() throws IOException {
    flush();
  }
}
