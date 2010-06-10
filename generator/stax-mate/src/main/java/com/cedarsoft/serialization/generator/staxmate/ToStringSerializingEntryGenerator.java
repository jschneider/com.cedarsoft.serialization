package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ToStringSerializingEntryGenerator implements SerializingEntryGenerator {
  @NonNls
  public static final String STRING_VALUE_OF = "valueOf";

  @Override
  public void appendSerializing( @NotNull JCodeModel model, @NotNull JMethod method, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldWithInitializationInfo fieldInfo ) {
    JClass jClass = model.ref( String.class );

    JInvocation getterInvocation = object.invoke( fieldInfo.getGetterDeclaration().getSimpleName() );
    JInvocation wrappedGetterInvocation = jClass.staticInvoke( STRING_VALUE_OF ).arg( getterInvocation );

    method.body().add(
      serializeTo.invoke( "addElementWithCharacters" )
        .arg( serializeTo.invoke( "getNamespace" ) )
        .arg( fieldInfo.getSimpleName() )
        .arg( wrappedGetterInvocation )
    );
  }

  @Override
  public void appendDeserializing( @NotNull JCodeModel model, @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldWithInitializationInfo fieldInfo ) {
    JVar varAsString = method.body().decl( model.ref( String.class ), getAsStringName( fieldInfo ),
                                           JExpr.invoke( "getChildText" ).arg( deserializeFrom ).arg( fieldInfo.getSimpleName() )
    );

    JClass fieldType = model.ref( fieldInfo.getType().toString() );
    JVar var = method.body().decl( fieldType, fieldInfo.getSimpleName(), createParseExpression( model, varAsString, fieldInfo ) );
  }

  @NotNull
  private JExpression createParseExpression( @NotNull JCodeModel model, @NotNull JVar varAsString, @NotNull FieldWithInitializationInfo fieldInfo ) {
    JInvocation parsingInvocatoin = model.ref( Double.class ).staticInvoke( "parse" );
    return parsingInvocatoin.arg( varAsString );
  }

  @NotNull
  @NonNls
  protected String getAsStringName( @NotNull FieldWithInitializationInfo fieldInfo ) {
    return fieldInfo.getSimpleName() + "AsString";
  }
}
