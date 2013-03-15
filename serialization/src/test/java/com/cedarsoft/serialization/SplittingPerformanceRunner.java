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

package com.cedarsoft.serialization;

import com.google.common.base.Splitter;
import org.apache.commons.lang.time.StopWatch;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 *
 */
public class SplittingPerformanceRunner {
  @Nonnull
  public static final Splitter SPLITTER = Splitter.on( "/" );
  public static final Splitter DOT_SPLITTER = Splitter.on( "." );

  public static void main( String[] args ) throws Exception {
    final String uri = "http://www.cedarsoft.com/some/slashes/1.0.0";

    run( "String.plit", new Callable<String>() {
      @Override
      public String call() throws Exception {
        String[] parts = uri.split( "/" );
        return parts[parts.length - 1];
      }
    } );

    run( "Splitter", new Callable<String>() {
      @Override
      public String call() throws Exception {
        Splitter splitter = Splitter.on( "/" );
        Iterable<String> parts = splitter.split( uri );


        Iterator<String> iterator = parts.iterator();
        while ( true ) {
          String current = iterator.next();
          if ( !iterator.hasNext() ) {
            return current;
          }
        }
      }
    } );

    run( "static Splitter", new Callable<String>() {
      @Override
      public String call() throws Exception {
        Iterable<String> parts = SPLITTER.split( uri );

        Iterator<String> iterator = parts.iterator();
        while ( true ) {
          String current = iterator.next();
          if ( !iterator.hasNext() ) {
            return current;
          }
        }
      }
    } );

    run( "indexOf", new Callable<String>() {
      @Override
      public String call() throws Exception {
        int index = uri.lastIndexOf( "/" );
        return uri.substring( index + 1 );
      }
    } );
  }

  private static void run( @Nonnull String description, @Nonnull Callable<String> callable ) throws Exception {
    //Warmup
    for ( int i = 0; i < 1000; i++ ) {
      assertEquals( "1.0.0", callable.call() );
    }

    //Do the work
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    for ( int i = 0; i < 100000; i++ ) {
      assertEquals( "1.0.0", callable.call() );
    }

    stopWatch.stop();
    System.out.println( description + " took " + stopWatch.getTime() );
  }

}
