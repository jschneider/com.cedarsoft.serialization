package com.cedarsoft.serialization.generator.output.decorators;

import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.MethodDecorator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class NotNullMethodDecorator implements MethodDecorator {
  @Override
  public void decorateSerializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod ) {
    annotateParamsWithNotNull( serializeMethod );
  }

  @Override
  public void decorateDeserializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod ) {
    deserializeMethod.annotate( NotNull.class );

    annotateParamsWithNotNull( deserializeMethod );
  }

  protected void annotateParamsWithNotNull( @NotNull JMethod method ) {
    for ( JVar param : method.listParams() ) {
      param.annotate( NotNull.class );
    }
  }
}
