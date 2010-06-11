package com.cedarsoft.serialization.generator.staxmate;

import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SerializingEntryCreators {
  @NotNull
  private final SerializingEntryGenerator toStringEntryGenerator;

  public SerializingEntryCreators( @NotNull JCodeModel model ) {
    toStringEntryGenerator = new ToStringSerializingEntryGenerator.AsElementGenerator( model );
  }

  @NotNull
  public SerializingEntryGenerator findGenerator() {
    return toStringEntryGenerator;
  }
}
