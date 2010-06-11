package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NotNull;

/**
   * Generates an attribute
   */
  public class AsAttributeGenerator implements SerializeToGenerator {
    @Override
    @NotNull
    public JInvocation createAddToSerializeToExpression( @NotNull JExpression serializeTo, @NotNull JExpression objectAsString, @NotNull FieldWithInitializationInfo fieldInfo ) {
      return serializeTo.invoke( "addAttribute" )
        .arg( fieldInfo.getSimpleName() )
        .arg( objectAsString );
    }

    @Override
    @NotNull
    public JInvocation createReadFromDeserializeFromExpression( @NotNull JExpression deserializeFrom, @NotNull FieldWithInitializationInfo fieldInfo ) {
      return deserializeFrom.invoke( "getAttributeValue" ).arg( JExpr._null() ).arg( fieldInfo.getSimpleName() );
    }
  }
