package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
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
  @NotNull
  protected final JCodeModel model;

  @NotNull
  private final ParseExpressionFactory parseExpressionFactory;

  public ToStringSerializingEntryGenerator( @NotNull JCodeModel model, @NotNull ParseExpressionFactory parseExpressionFactory ) {
    this.model = model;
    this.parseExpressionFactory = parseExpressionFactory;
  }

  @Override
  public void appendSerializing( @NotNull JMethod method, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldWithInitializationInfo fieldInfo ) {
    JClass jClass = model.ref( String.class );

    JInvocation getterInvocation = object.invoke( fieldInfo.getGetterDeclaration().getSimpleName() );
    JInvocation wrappedGetterInvocation;
    if ( fieldInfo.isType( String.class ) ) {
      wrappedGetterInvocation = getterInvocation;
    } else {
      wrappedGetterInvocation = jClass.staticInvoke( STRING_VALUE_OF ).arg( getterInvocation );
    }

    SerializeToGenerator serializeToHandler = getStrategy( fieldInfo );
    method.body().add( serializeToHandler.createAddToSerializeToExpression( serializeTo, wrappedGetterInvocation, fieldInfo ) );
  }

  @NotNull
  @Override
  public JVar appendDeserializing( @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldWithInitializationInfo fieldInfo ) {
    SerializeToGenerator serializeToHandler = getStrategy( fieldInfo );

    JInvocation readToStringExpression = serializeToHandler.createReadFromDeserializeFromExpression( deserializeFrom, fieldInfo );

    JClass fieldType = model.ref( fieldInfo.getType().toString() );
    return method.body().decl( fieldType, fieldInfo.getSimpleName(), parseExpressionFactory.createParseExpression( readToStringExpression, fieldInfo ) );
  }

  @NotNull
  private SerializeToGenerator getStrategy( @NotNull FieldWithInitializationInfo fieldInfo ) {
    if ( fieldInfo.isType( Integer.TYPE ) ) {
      return asAttribute;
    }
    return asElement;
  }


  @NotNull
  private final SerializeToGenerator asElement = new AsElementGenerator();
  @NotNull
  private final SerializeToGenerator asAttribute = new AsAttributeGenerator();

}
