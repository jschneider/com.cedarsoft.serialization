package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SerializingEntryCreators {
  @NotNull
  private final SerializingEntryGenerator toStringEntryGenerator = new ToStringSerializingEntryGenerator();

  @NotNull
  public SerializingEntryGenerator findGenerator( @NotNull FieldWithInitializationInfo type ) {
    return toStringEntryGenerator;
  }
}
