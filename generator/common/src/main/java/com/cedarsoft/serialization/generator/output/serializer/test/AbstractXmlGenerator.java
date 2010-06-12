package com.cedarsoft.serialization.generator.output.serializer.test;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractXmlGenerator extends AbstractGenerator<XmlDecisionCallback> {
  public AbstractXmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

}
