package com.cedarsoft.serialization.generator.output.serializer.test;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractXmlGenerator extends AbstractGenerator<XmlDecisionCallback> {
  @NonNls
  public static final String METHOD_NAME_GET_EXPECTED_SERIALIZED = "getExpectedSerialized";

  protected AbstractXmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @NotNull
  protected JMethod createVerifyMethod( @NotNull JDefinedClass serializerTestClass, @NotNull JClass serializerClass, @NotNull JClass domainType ) {
    JMethod method = serializerTestClass.method( JMod.PROTECTED, String.class, METHOD_NAME_GET_EXPECTED_SERIALIZED );
    method.annotate( Override.class );

    method.body()._return( JExpr.lit( "<implementMe/>" ) );

    return method;
  }
}
