package com.cedarsoft.serialization.generator.output.staxmate.serializer.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.serializer.test.AbstractXmlGenerator;
import com.sun.codemodel.JClass;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class StaxMateGenerator extends AbstractXmlGenerator {
  public StaxMateGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @NotNull
  @Override
  protected JClass createExtendsClass( @NotNull JClass domainType, @NotNull JClass serializerClass ) {
    return codeModel.ref( AbstractXmlSerializerTest.class ).narrow( domainType );
  }
}
