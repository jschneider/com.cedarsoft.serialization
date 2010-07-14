package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class DeserializationContext {
  @NotNull
  private final Version formatVersion;

  public DeserializationContext( @NotNull Version formatVersion ) {
    this.formatVersion = formatVersion;
  }

  @NotNull
  public Version getFormatVersion() {
    return formatVersion;
  }
}
