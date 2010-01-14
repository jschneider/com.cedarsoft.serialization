package com.cedarsoft.serialization.bench;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.Serializable;

/**
 *
 */
public class Extension implements Serializable {
  @NonNls
  public static final String DEFAULT_DELIMITER = ".";


  @Attribute( name = "default" )
  private final boolean isDefault;
  @Attribute( name = "delimiter" )
  @NotNull
  @NonNls
  private final String delimiter;
  @Element( name = "extension" )
  @NotNull
  @NonNls
  private final String extension;

  public Extension( @Attribute( name = "delimiter" ) @NotNull String delimiter, @Element( name = "extension" ) @NotNull String extension, @Attribute( name = "default" ) boolean isDefault ) {
    this.delimiter = delimiter;
    this.extension = extension;
    this.isDefault = isDefault;
  }

  public boolean isDefault() {
    return isDefault;
  }

  @NotNull
  public String getDelimiter() {
    return delimiter;
  }

  @NotNull
  public String getExtension() {
    return extension;
  }
}
