package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.ParseExpressionFactory;
import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SerializingEntryCreators {
  @NotNull
  private final SerializingEntryGenerator toStringEntryGenerator;

  public SerializingEntryCreators( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    toStringEntryGenerator = new ToStringSerializingEntryGenerator( codeGenerator );
  }

  @NotNull
  public SerializingEntryGenerator findGenerator() {
    return toStringEntryGenerator;
  }
}
