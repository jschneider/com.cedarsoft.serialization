package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Creates parse expressions for the different field types
 */
public class ParseExpressionFactory {
  @NotNull
  private final JCodeModel model;

  public ParseExpressionFactory( @NotNull JCodeModel model ) {
    this.model = model;
  }

  @NotNull
  public JExpression createParseExpression( @NotNull JExpression varAsString, @NotNull FieldWithInitializationInfo fieldInfo ) {
    if ( fieldInfo.isType( Double.TYPE ) || fieldInfo.isType( Double.class ) ) {
      return model.ref( Double.class ).staticInvoke( "parseDouble" ).arg( varAsString );
    }

    if ( fieldInfo.isType( Integer.TYPE ) || fieldInfo.isType( Integer.class ) ) {
      return model.ref( Integer.class ).staticInvoke( "parseInt" ).arg( varAsString );
    }

    if ( fieldInfo.isType( String.class ) ) {
      return varAsString;
    }

    //Fallback to a generic parse method
    JClass fieldType = model.ref( fieldInfo.getType().toString() );
    return JExpr.invoke( "parse" + fieldType.name() ).arg( varAsString );
  }
}
