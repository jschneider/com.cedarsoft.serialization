package com.cedarsoft.serialization.jackson;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class InvalidTypeException extends Exception {
  @Nullable
  @NonNls
  private final String type;
  @NotNull
  @NonNls
  private final String expected;

  public InvalidTypeException( @Nullable @NonNls String type, @NonNls @NotNull String expected ) {
    super( "Invalid type. Was <" + type + "> but expected <" + expected + ">" );
    this.type = type;
    this.expected = expected;
  }

  @NonNls
  @Nullable
  public String getType() {
    return type;
  }

  @NotNull
  @NonNls
  public String getExpected() {
    return expected;
  }
}
