package com.cedarsoft.serialization.generator.output.staxmate.serializer;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.NamingSupport;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractDelegateGenerator extends AbstractSerializeToGenerator {
  @NonNls
  public static final String METHOD_NAME_ADD_ELEMENT = "addElement";
  @NonNls
  public static final String METHOD_NAME_GET_NAMESPACE = "getNamespace";
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

  @NotNull
  protected JInvocation createAddElementExpression( @NotNull JExpression serializeTo, @NotNull JExpression elementName ) {
    return serializeTo.invoke( METHOD_NAME_ADD_ELEMENT ).arg( serializeTo.invoke( METHOD_NAME_GET_NAMESPACE ) ).arg( elementName );
  }
}
