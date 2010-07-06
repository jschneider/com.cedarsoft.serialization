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
