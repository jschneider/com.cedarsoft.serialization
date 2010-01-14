package com.cedarsoft.serialization.bench;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 *
 */
@Root
public class FileType implements Serializable {
  @Attribute( name = "dependent" )
  private final boolean dependent;
  @Element( name = "id" )
  @NotNull
  @NonNls
  private final String id;
  @Element( name = "extension" )
  @NotNull
  private final Extension extension;

  public FileType( @Element( name = "id" ) @NotNull String id, @Element( name = "extension" ) @NotNull Extension extension, @Attribute( name = "dependent" ) boolean dependent ) {
    this.dependent = dependent;
    this.id = id;
    this.extension = extension;
  }

  @NotNull
  public Extension getExtension() {
    return extension;
  }

  public boolean isDependent() {
    return dependent;
  }

  @NotNull
  public String getId() {
    return id;
  }
}
