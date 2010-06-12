package com.cedarsoft.serialization.generator.output.decorators;

import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.Decorator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

/**
 *
 */
public class NotNullDecorator implements Decorator {
  @NotNull
  private final Class<? extends Annotation> notNullAnnotationType;

  public NotNullDecorator( @NotNull Class<? extends Annotation> notNullAnnotationType ) {
    this.notNullAnnotationType = notNullAnnotationType;
  }

  @Override
  public void decorateSerializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod ) {
    annotateParamsWithNotNull( serializeMethod );
  }

  @Override
  public void decorateDeserializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod ) {
    deserializeMethod.annotate( notNullAnnotationType );

    annotateParamsWithNotNull( deserializeMethod );
  }

  @Override
  public void decorateConstant( @NotNull CodeGenerator<?> codeGenerator, @NotNull JFieldVar constant ) {
    constant.annotate( notNullAnnotationType );
  }

  protected void annotateParamsWithNotNull( @NotNull JMethod method ) {
    for ( JVar param : method.listParams() ) {
      param.annotate( notNullAnnotationType );
    }
  }
}
