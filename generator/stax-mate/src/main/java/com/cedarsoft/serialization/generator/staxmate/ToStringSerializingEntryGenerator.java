package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.model.FieldInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.SerializeToGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ToStringSerializingEntryGenerator implements SerializingEntryGenerator {
  @NotNull
  private final CodeGenerator<XmlDecisionCallback> codeGenerator;

  public ToStringSerializingEntryGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    this.codeGenerator = codeGenerator;
  }

  @Override
  public void appendSerializing( @NotNull JDefinedClass serializerClass, @NotNull JMethod method, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldDeclarationInfo fieldInfo ) {
    method.body().directStatement( "//" + fieldInfo.getSimpleName() );

    JExpression objectAsString = codeGenerator.getParseExpressionFactory().createToStringExpression( object.invoke( fieldInfo.getGetterDeclaration().getSimpleName() ), fieldInfo );

    SerializeToGenerator serializeToHandler = getStrategy( fieldInfo );
    method.body().add( serializeToHandler.createAddToSerializeToExpression(serializerClass, serializeTo, objectAsString, fieldInfo ) );
  }

  @NotNull
  @Override
  public JVar appendDeserializing( @NotNull JDefinedClass serializerClass, @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    method.body().directStatement( "//" + fieldInfo.getSimpleName() );
    SerializeToGenerator serializeToHandler = getStrategy( fieldInfo );

    JExpression readToStringExpression = serializeToHandler.createReadFromDeserializeFromExpression(serializerClass, deserializeFrom, fieldInfo );

    JClass fieldType = codeGenerator.getModel().ref( fieldInfo.getType().toString() );
    return method.body().decl( fieldType, fieldInfo.getSimpleName(), codeGenerator.getParseExpressionFactory().createParseExpression( readToStringExpression, fieldInfo ) );
  }

  @NotNull
  private SerializeToGenerator getStrategy( @NotNull FieldInfo fieldInfo ) {
    XmlDecisionCallback.Target target = codeGenerator.getDecisionCallback().getSerializationTarget( fieldInfo );
    switch ( target ) {
      case ELEMENT:
        return asElement;
      case ATTRIBUTE:
        return asAttribute;
    }

    throw new IllegalStateException( "Should not reach! " + fieldInfo );
  }

  @NotNull
  private final SerializeToGenerator asElement = new AsElementGenerator();
  @NotNull
  private final SerializeToGenerator asAttribute = new AsAttributeGenerator();

}
