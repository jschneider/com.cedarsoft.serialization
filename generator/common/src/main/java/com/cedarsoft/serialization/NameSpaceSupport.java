package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class NameSpaceSupport {
  @NotNull
  @NonNls
  public static String createNameSpaceUriBase( @NotNull Class<?> type ) {
    String[] parts = type.getName().split( "\\." );

    //If we have lesser than three parts just return the type - a fallback
    if ( parts.length < 3 ) {
      return "http://" + type.getName();
    }

    StringBuilder uri = new StringBuilder( "http://www." );
    uri.append( parts[1] );
    uri.append( "." );
    uri.append( parts[0] );

    for ( int i = 2, partsLength = parts.length; i < partsLength; i++ ) {
      String part = parts[i];
      uri.append( "/" );
      uri.append( part );
    }

    return uri.toString();
  }
}
