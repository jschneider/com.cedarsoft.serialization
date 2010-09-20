package com.cedarsoft.serialization;

import com.google.common.base.Splitter;
import org.apache.commons.lang.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 *
 */
public class SplittingPerformanceRunner {
  @NotNull
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

  private static void run( @NotNull String description, @NotNull Callable<String> callable ) throws Exception {
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
