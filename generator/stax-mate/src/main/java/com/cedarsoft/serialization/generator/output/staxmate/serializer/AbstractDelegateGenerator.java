package com.cedarsoft.serialization.generator.output.staxmate.serializer;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractDelegateGenerator extends AbstractSerializeToGenerator {
  @NonNls
  public static final String METHOD_NAME_NEXT_TAG = "nextTag";
  @NonNls
  public static final String METHOD_NAME_CLOSE_TAG = "closeTag";
  
  protected AbstractDelegateGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @NotNull
  protected JInvocation createCloseTagInvocation( @NotNull JExpression deserializeFrom ) {
    return JExpr.invoke( METHOD_NAME_CLOSE_TAG ).arg( deserializeFrom );
  }

  @NotNull
  protected JInvocation createNextTagInvocation( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull FieldDeclarationInfo fieldInfo ) {
    return JExpr.invoke( METHOD_NAME_NEXT_TAG ).arg( deserializeFrom ).arg( getConstant( serializerClass, fieldInfo ) );
  }
}
