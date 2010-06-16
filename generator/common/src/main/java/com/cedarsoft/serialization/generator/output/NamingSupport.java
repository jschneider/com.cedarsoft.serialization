package com.cedarsoft.serialization.generator.output;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class NamingSupport {
  private NamingSupport() {

  }

  @NotNull
  @NonNls
  public static String createXmlElementName( @NotNull @NonNls String simpleClassName ) {
    return simpleClassName.toLowerCase();
  }

  @NotNull
  @NonNls
  public static String createVarName( @NotNull @NonNls String simpleClassName ) {
    if ( simpleClassName.length() == 0 ) {
      throw new IllegalArgumentException( "Invalid class name: Is empty" );
    }

    return simpleClassName.substring( 0, 1 ).toLowerCase() + simpleClassName.substring( 1 );
  }

  @NotNull
  @NonNls
  public static String createSetter( @NotNull @NonNls String fieldName ) {
    return "set" + fieldName.substring( 0, 1 ).toUpperCase() + fieldName.substring( 1 );
  }
}
