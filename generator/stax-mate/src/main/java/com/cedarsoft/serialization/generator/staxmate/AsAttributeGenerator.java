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
 * Generates an attribute
 */
public class AsAttributeGenerator implements SerializeToGenerator {
  @NonNls
  public static final String METHOD_NAME_ADD_ATTRIBUTE = "addAttribute";
  @NonNls
  public static final String METHOD_NAME_GET_ATTRIBUTE_VALUE = "getAttributeValue";

  @NotNull
  private final CodeGenerator<XmlDecisionCallback> codeGenerator;

  public AsAttributeGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    this.codeGenerator = codeGenerator;
  }

  @Override
  @NotNull
  public JInvocation createAddToSerializeToExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression serializeTo, @NotNull JExpression objectAsString, @NotNull FieldDeclarationInfo fieldInfo ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    return serializeTo.invoke( METHOD_NAME_ADD_ATTRIBUTE )
      .arg( constant )
      .arg( objectAsString );
  }

  @Override
  @NotNull
  public JInvocation createReadFromDeserializeFromExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull FieldDeclarationInfo fieldInfo ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    return deserializeFrom.invoke( METHOD_NAME_GET_ATTRIBUTE_VALUE ).arg( JExpr._null() ).arg( constant );
  }

  @NotNull
  private JFieldVar getConstant( @NotNull JDefinedClass serializerClass, @NotNull FieldInfo fieldInfo ) {
    return codeGenerator.getOrCreateConstant( serializerClass, String.class, getConstantName( fieldInfo ), JExpr.lit( fieldInfo.getSimpleName() ) );
  }

  @NotNull
  @NonNls
  protected String getConstantName( @NotNull FieldInfo fieldInfo ) {
    return "ATTRIBUTE_" + fieldInfo.getSimpleName().toUpperCase();
  }
}
