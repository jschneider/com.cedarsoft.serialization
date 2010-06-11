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
  @NotNull
  protected final JCodeModel model;

  public ToStringSerializingEntryGenerator( @NotNull JCodeModel model ) {
    this.model = model;
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

    Strategy strategy = getStrategy( fieldInfo );
    method.body().add( strategy.createSerializeInvocation( serializeTo, wrappedGetterInvocation, fieldInfo ) );
  }

  @NotNull
  @Override
  public JVar appendDeserializing( @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldWithInitializationInfo fieldInfo ) {
    JInvocation readToStringExpression = JExpr.invoke( "getChildText" ).arg( deserializeFrom ).arg( fieldInfo.getSimpleName() );

    JClass fieldType = model.ref( fieldInfo.getType().toString() );
    return method.body().decl( fieldType, fieldInfo.getSimpleName(), createParseExpression( readToStringExpression, fieldInfo ) );
  }

  @NotNull
  private JExpression createParseExpression( @NotNull JExpression varAsString, @NotNull FieldWithInitializationInfo fieldInfo ) {
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

  @NotNull
  private Strategy getStrategy( @NotNull FieldWithInitializationInfo fieldInfo ) {
    if ( fieldInfo.isType( Integer.TYPE ) ) {
      return asAttribute;
    }
    return asElement;
  }


  @NotNull
  private final Strategy asElement = new AsElementGenerator();
  @NotNull
  private final Strategy asAttribute = new AsAttributeGenerator();

  public interface Strategy {
    /**
     * Creates the serialize invocation
     *
     * @param serializeTo    the serialize to var
     * @param objectAsString the getter invocation used to get the value as string
     * @param fieldInfo      the field info
     * @return the invocation the serializes the given
     */
    @NotNull
    JInvocation createSerializeInvocation( @NotNull JVar serializeTo, @NotNull JExpression objectAsString, @NotNull FieldWithInitializationInfo fieldInfo );
  }

  /**
   * Generates a new element
   */
  public static class AsElementGenerator implements Strategy {
    @Override
    @NotNull
    public JInvocation createSerializeInvocation( @NotNull JVar serializeTo, @NotNull JExpression objectAsString, @NotNull FieldWithInitializationInfo fieldInfo ) {
      return serializeTo.invoke( "addElementWithCharacters" )
        .arg( serializeTo.invoke( "getNamespace" ) )
        .arg( fieldInfo.getSimpleName() )
        .arg( objectAsString );
    }
  }

  /**
   * Generates an attribute
   */
  public static class AsAttributeGenerator implements Strategy {
    @Override
    @NotNull
    public JInvocation createSerializeInvocation( @NotNull JVar serializeTo, @NotNull JExpression objectAsString, @NotNull FieldWithInitializationInfo fieldInfo ) {
      return serializeTo.invoke( "addAttribute" )
        .arg( fieldInfo.getSimpleName() )
        .arg( objectAsString );
    }
  }
}
