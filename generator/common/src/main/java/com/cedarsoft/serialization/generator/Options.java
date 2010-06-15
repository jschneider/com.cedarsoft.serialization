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

//package com.cedarsoft.serialization.generator;
//
//import org.jetbrains.annotations.NonNls;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// *
// */
//public class Options {
//
//  // honor CLASSPATH environment variable, but it will be overrided by -cp
//  @Nullable
//  @NonNls
//  private String classpath = System.getenv( "CLASSPATH" );
//  @Nullable
//  private File outDir;
//  @Nullable
//  private File testOutDir;
//
//  @NotNull
//  @NonNls
//  private final List<String> arguments = new ArrayList<String>();
//
//  public Options( @NotNull @NonNls String[] args ) throws BadCommandLineException {
//    for ( int i = 0; i < args.length; i++ ) {
//      if ( args[i].charAt( 0 ) == '-' ) {
//        int j = parseArgument( args, i );
//        if ( j == 0 ) {
//          throw new BadCommandLineException( Messages.UNRECOGNIZED_PARAMETER.format( args[i] ) );
//        }
//        i += j;
//      } else {
//        arguments.add( args[i] );
//      }
//    }
//  }
//
//  private int parseArgument( @NotNull @NonNls String[] args, int index ) throws BadCommandLineException {
//    if ( args[index].equals( "-d" ) ) {
//      if ( index == args.length - 1 ) {
//        throw new BadCommandLineException( Messages.OPERAND_MISSING.format( args[index] ) );
//      }
//      outDir = new File( args[++index] );
//      if ( !outDir.exists() ) {
//        throw new BadCommandLineException(
//          Messages.NON_EXISTENT_FILE.format( targetDir ) );
//      }
//      return 1;
//    }
//
//    if ( args[index].equals( "-episode" ) ) {
//      if ( index == args.length - 1 ) {
//        throw new BadCommandLineException( Messages.OPERAND_MISSING.format( args[index] ) );
//      }
//      episodeFile = new File( args[++index] );
//      return 1;
//    }
//
//    if ( args[index].equals( "-encoding" ) ) {
//      if ( index == args.length - 1 ) {
//        throw new BadCommandLineException( Messages.OPERAND_MISSING.format( args[index] ) );
//      }
//      encoding = args[++index];
//      return 1;
//    }
//
//    if ( args[index].equals( "-cp" ) || args[index].equals( "-classpath" ) ) {
//      if ( index == args.length - 1 ) {
//        throw new BadCommandLineException( Messages.OPERAND_MISSING.format( args[index] ) );
//      }
//      classpath = args[++index];
//
//      return 1;
//    }
//
//    return 0;
//  }
//
//  @Nullable
//  @NonNls
//  public String getClasspath() {
//    return classpath;
//  }
//
//  @Nullable
//  public File getTargetDir() {
//    return targetDir;
//  }
//
//  @Nullable
//  public File getEpisodeFile() {
//    return episodeFile;
//  }
//
//  @NotNull
//  @NonNls
//  public String getEncoding() {
//    return encoding;
//  }
//
//  @NotNull
//  @NonNls
//  public List<? extends String> getArguments() {
//    return Collections.unmodifiableList( arguments );
//  }
//
//}
