package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.model.FieldInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.SerializeToGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Generates a new element
 */
public class AsElementGenerator implements SerializeToGenerator {
  @NotNull
  private final CodeGenerator<XmlDecisionCallback> codeGenerator;
  @NonNls
  public static final String METHOD_NAME_GET_CHILD_TEXT = "getChildText";
  @NonNls
  public static final String METHOD_NAME_GET_NAMESPACE = "getNamespace";
  @NonNls
  public static final String METHOD_NAME_ADD_ELEMENT_WITH_CHARACTERS = "addElementWithCharacters";

  public AsElementGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    this.codeGenerator = codeGenerator;
  }

  @Override
  @NotNull
  public JInvocation createAddToSerializeToExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression serializeTo, @NotNull JExpression objectAsString, @NotNull FieldDeclarationInfo fieldInfo ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    return serializeTo.invoke( METHOD_NAME_ADD_ELEMENT_WITH_CHARACTERS )
      .arg( serializeTo.invoke( METHOD_NAME_GET_NAMESPACE ) )
      .arg( constant )
      .arg( objectAsString );
  }

  @Override
  @NotNull
  public JInvocation createReadFromDeserializeFromExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull FieldDeclarationInfo fieldInfo ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );
    return JExpr.invoke( METHOD_NAME_GET_CHILD_TEXT ).arg( deserializeFrom ).arg( constant );
  }

  @NotNull
  private JFieldVar getConstant( @NotNull JDefinedClass serializerClass, @NotNull FieldInfo fieldInfo ) {
    return codeGenerator.getOrCreateConstant( serializerClass, String.class, getConstantName( fieldInfo ), JExpr.lit( fieldInfo.getSimpleName() ) );
  }

  @NotNull
  @NonNls
  protected String getConstantName( @NotNull FieldInfo fieldInfo ) {
    return "ELEMENT_" + fieldInfo.getSimpleName().toUpperCase();
  }
}
