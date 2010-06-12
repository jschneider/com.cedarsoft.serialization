package com.cedarsoft.serialization.generator.output.serializer.test;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.GeneratorBase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @param <T>
 */
public abstract class AbstractGenerator<T extends DecisionCallback> extends GeneratorBase<T> {
  @NonNls
  @NotNull
  public static final String SERIALIZER_TEST_CLASS_NAME_SUFFIX = "Test";

  protected AbstractGenerator( @NotNull CodeGenerator<T> codeGenerator ) {
    super( codeGenerator );
  }

  public JDefinedClass generateSerializerTest( @NotNull JClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );

    //the class
    String fullyqualifiedName = createSerializerClassTestName( serializerClass.fullName() );
    JDefinedClass serializerTestClass = codeModel._class( fullyqualifiedName )._extends( createExtendsClass() );


    return serializerTestClass;
  }

  @NotNull
  protected abstract JClass createExtendsClass();

  @NotNull
  @NonNls
  public String createSerializerClassTestName( @NotNull @NonNls String serializerClassName ) {
    return serializerClassName + SERIALIZER_TEST_CLASS_NAME_SUFFIX;
  }
}
