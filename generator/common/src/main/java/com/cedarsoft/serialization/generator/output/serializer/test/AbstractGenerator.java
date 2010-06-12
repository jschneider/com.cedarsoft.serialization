package com.cedarsoft.serialization.generator.output.serializer.test;

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.GeneratorBase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @param <T>
 */
public abstract class AbstractGenerator<T extends DecisionCallback> extends GeneratorBase<T> {
  @NonNls
  @NotNull
  public static final String SERIALIZER_TEST_CLASS_NAME_SUFFIX = "Test";
  @NonNls
  public static final String METHOD_NAME_GET_SERIALIZER = "getSerializer";
  @NonNls
  public static final String METHOD_NAME_CREATE_OBJECT_TO_SERIALIZE = "createObjectToSerialize";

  protected AbstractGenerator( @NotNull CodeGenerator<T> codeGenerator ) {
    super( codeGenerator );
  }

  public JDefinedClass generateSerializerTest( @NotNull JClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );

    //the class
    JDefinedClass serializerTestClass = codeModel._class( createSerializerClassTestName( serializerClass.fullName() ) )._extends( createExtendsClass( domainType, serializerClass ) );


    //getSerializer
    createGetSerializerMethod( serializerTestClass, serializerClass, domainType );

    //createObjectToSerialize
    createCreateObjectToSerializeMethod( serializerTestClass, serializerClass, domainType );

    //Create the verify method
    createVerifyMethod( serializerTestClass, serializerClass, domainType );

    return serializerTestClass;
  }

  @NotNull
  protected JMethod createCreateObjectToSerializeMethod( @NotNull JDefinedClass serializerTestClass, @NotNull JClass serializerClass, @NotNull JClass domainType ) {
    JMethod method = serializerTestClass.method( JMod.PROTECTED, domainType, METHOD_NAME_CREATE_OBJECT_TO_SERIALIZE )._throws( Exception.class );
    method.annotate( Override.class );

    method.body()._return( JExpr._new( domainType ) );
    return method;
  }

  @NotNull
  protected JMethod createGetSerializerMethod( @NotNull JDefinedClass serializerTestClass, @NotNull JClass serializerClass, @NotNull JClass domainType ) {
    JType returnType = codeModel.ref( Serializer.class ).narrow( domainType );
    JMethod createSerializerMethod = serializerTestClass.method( JMod.PROTECTED, returnType, METHOD_NAME_GET_SERIALIZER )._throws( Exception.class );
    createSerializerMethod.annotate( Override.class );

    //Return the serializer
    createSerializerMethod.body()._return( JExpr._new( serializerClass ) );

    return createSerializerMethod;
  }

  @NotNull
  protected abstract JMethod createVerifyMethod( @NotNull JDefinedClass serializerTestClass, @NotNull JClass serializerClass, @NotNull JClass domainType );

  @NotNull
  protected abstract JClass createExtendsClass( @NotNull JClass domainType, @NotNull JClass serializerClass );

  @NotNull
  @NonNls
  public String createSerializerClassTestName( @NotNull @NonNls String serializerClassName ) {
    return serializerClassName + SERIALIZER_TEST_CLASS_NAME_SUFFIX;
  }
}
