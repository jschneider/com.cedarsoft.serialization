package com.cedarsoft.serialization.generator;

/**
*
*/
public class BadCommandLineException extends Exception {
  public BadCommandLineException() {
  }

  public BadCommandLineException( String message ) {
    super( message );
  }

  public BadCommandLineException( String message, Throwable cause ) {
    super( message, cause );
  }

  public BadCommandLineException( Throwable cause ) {
    super( cause );
  }
}
