package com.cedarsoft.serialization;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Exception that is thrown on an invalid name space
 */
public class InvalidNamespaceException extends Exception {
  @NotNull
  @NonNls
  private final String namespace;
  @NotNull
  @NonNls
  private final String expected;

  public InvalidNamespaceException( @NotNull @NonNls String namespace, @NonNls @NotNull String expected ) {
    super( "Invalid namespace. Was <" + namespace + "> but expected <" + expected + ">" );
    this.namespace = namespace;
    this.expected = expected;
  }

  @NotNull
  @NonNls
  public String getNamespace() {
    return namespace;
  }

  @NotNull
  @NonNls
  public String getExpected() {
    return expected;
  }
}
