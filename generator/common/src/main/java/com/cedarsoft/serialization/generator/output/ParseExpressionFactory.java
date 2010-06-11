package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.model.FieldTypeInformation;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Creates parse expressions for the different field types
 */
public class ParseExpressionFactory {
  @NonNls
  public static final String STRING_VALUE_OF = "valueOf";

  @NotNull
  private final JCodeModel model;

  public ParseExpressionFactory( @NotNull JCodeModel model ) {
    this.model = model;
  }

  @NotNull
  public JExpression createParseExpression( @NotNull JExpression varAsString, @NotNull FieldTypeInformation fieldInfo ) {
    if ( fieldInfo.isType( Double.TYPE ) || fieldInfo.isType( Double.class ) ) {
      return model.ref( Double.class ).staticInvoke( "parseDouble" ).arg( varAsString );
    }

    if ( fieldInfo.isType( Integer.TYPE ) || fieldInfo.isType( Integer.class ) ) {
      return model.ref( Integer.class ).staticInvoke( "parseInt" ).arg( varAsString );
    }

    if ( fieldInfo.isType( Float.TYPE ) || fieldInfo.isType( Float.class ) ) {
      return model.ref( Float.class ).staticInvoke( "parseFloat" ).arg( varAsString );
    }

    if ( fieldInfo.isType( Boolean.TYPE ) || fieldInfo.isType( Boolean.class ) ) {
      return model.ref( Boolean.class ).staticInvoke( "parseBoolean" ).arg( varAsString );
    }

    if ( fieldInfo.isType( String.class ) ) {
      return varAsString;
    }

    //Fallback to a generic parse method
    JClass fieldType = model.ref( fieldInfo.getType().toString() );
    return JExpr.invoke( "parse" + fieldType.name() ).arg( varAsString );
  }

  @NotNull
  public JExpression createToStringExpression( @NotNull JInvocation getterInvocation, @NotNull FieldTypeInformation fieldInfo ) {
    JClass jClass = model.ref( String.class );
    JInvocation wrappedGetterInvocation;
    if ( fieldInfo.isType( String.class ) ) {
      wrappedGetterInvocation = getterInvocation;
    } else {
      wrappedGetterInvocation = jClass.staticInvoke( STRING_VALUE_OF ).arg( getterInvocation );
    }
    return wrappedGetterInvocation;
  }
}
