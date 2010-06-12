package com.cedarsoft.serialization.generator.output.decorators;

import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.Decorator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

/**
 *
 */
public class I18nAnnotationsDecorator implements Decorator {
  @NotNull
  private final Class<? extends Annotation> annotation;

  public I18nAnnotationsDecorator( @NotNull Class<? extends Annotation> annotation ) {
    this.annotation = annotation;
  }

  @Override
  public void decorateSerializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod ) {
  }

  @Override
  public void decorateDeserializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod ) {
  }

  @Override
  public void decorateConstant( @NotNull CodeGenerator<?> codeGenerator, @NotNull JFieldVar constant ) {
    constant.annotate( annotation );
  }
}
