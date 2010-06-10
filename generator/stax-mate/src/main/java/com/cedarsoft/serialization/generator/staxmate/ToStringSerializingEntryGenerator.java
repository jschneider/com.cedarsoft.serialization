package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ToStringSerializingEntryGenerator implements SerializingEntryGenerator {
  @Override
  public void appendSerializing( @NotNull JCodeModel model, @NotNull JMethod serializeMethod, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldWithInitializationInfo fieldInfo ) {
    TypeMirror type = fieldInfo.getType();

    //    return type instanceof DeclaredType && ( ( DeclaredType ) type ).getDeclaration().getQualifiedName().equals( String.class.getName() );

    JClass jClass = model.ref( String.class );

    JInvocation getterInvocation = object.invoke( fieldInfo.getGetterDeclaration().getSimpleName() );
    JInvocation wrappedGetterInvocation = jClass.staticInvoke( "valueOf" ).arg( getterInvocation );

    serializeMethod.body().add(
      serializeTo.invoke( "addElementWithCharacters" )
        .arg( serializeTo.invoke( "getNamespace" ) )
        .arg( fieldInfo.getSimpleName() )
        .arg( wrappedGetterInvocation )
    );
  }
}
