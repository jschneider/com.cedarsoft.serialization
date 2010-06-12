package com.cedarsoft.serialization.generator.output;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface Decorator {
  void decorateSerializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod );

  void decorateDeserializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod );
}
