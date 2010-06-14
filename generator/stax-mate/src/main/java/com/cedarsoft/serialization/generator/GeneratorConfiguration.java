package com.cedarsoft.serialization.generator;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 *
 */
public class GeneratorConfiguration {
  @NotNull
  private final File domainSourceFile;
  @NotNull
  private final File destination;
  @NotNull
  private final File testDestination;

  public GeneratorConfiguration( @NotNull File domainSourceFile, @NotNull File destination, @NotNull File testDestination ) {
    this.domainSourceFile = domainSourceFile;
    this.destination = destination;
    this.testDestination = testDestination;
  }

  @NotNull
  public File getDomainSourceFile() {
    return domainSourceFile;
  }

  @NotNull
  public File getDestination() {
    return destination;
  }

  @NotNull
  public File getTestDestination() {
    return testDestination;
  }
}
