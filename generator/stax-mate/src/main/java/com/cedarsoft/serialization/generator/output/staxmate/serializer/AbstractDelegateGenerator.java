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

  protected void addDelegatingSerializerToConstructor( @NotNull JDefinedClass serializerClass, @NotNull JClass fieldType ) {
    JType fieldSerializerType = getSerializerRefFor( fieldType );

    JMethod constructor = ( JMethod ) serializerClass.constructors().next();
    String paramName = NamingSupport.createVarName( fieldSerializerType.name() );

    //Check whether the serializer still exists
    for ( JVar param : constructor.listParams() ) {
      if ( param.type().equals( fieldSerializerType ) ) {
        return;
      }
    }

    //It does not exist, therefore let us add the serializer and map it
    JVar param = constructor.param( fieldSerializerType, paramName );

    constructor.body().add( JExpr.invoke( "add" ).arg( param ).invoke( "responsibleFor" ).arg( JExpr.dotclass( fieldType ) )
      .invoke( "map" )
      .arg( JExpr.lit( 1 ) ).arg( JExpr.lit( 0 ) ).arg( JExpr.lit( 0 ) )
      .invoke( "toDelegateVersion" )
      .arg( JExpr.lit( 1 ) ).arg( JExpr.lit( 0 ) ).arg( JExpr.lit( 0 ) ) );
  }

  @NotNull
  protected JClass getSerializerRefFor( @NotNull JType type ) {
    return codeGenerator.ref( type.fullName() + "Serializer" );
  }
}
