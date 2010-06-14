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
