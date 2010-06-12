package com.cedarsoft.serialization.generator.output;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import org.jetbrains.annotations.NotNull;

/**
 * Decorators can be used to generate additional code (e.g. annotations, comments etc.)
 */
public interface Decorator {
  /**
   * Decorates the serialize method
   *
   * @param codeGenerator   the code generator
   * @param domainType      the domain type
   * @param serializerClass the serializer class
   * @param serializeMethod the serialize method
   */
  void decorateSerializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod );

  /**
   * Decorates the deserialize method
   *
   * @param codeGenerator     the code generator
   * @param domainType        the domain type
   * @param serializerClass   the serializer class
   * @param deserializeMethod the deserialize method
   */
  void decorateDeserializeMethod( @NotNull CodeGenerator<?> codeGenerator, @NotNull JType domainType, @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod );

  /**
   * Decorates constants
   *
   * @param codeGenerator the code generator
   * @param constant      the constant
   */
  void decorateConstant( @NotNull CodeGenerator<?> codeGenerator, @NotNull JFieldVar constant );
}
