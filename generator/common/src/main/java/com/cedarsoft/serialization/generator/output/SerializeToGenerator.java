package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import org.jetbrains.annotations.NotNull;

/**
 * Interface that is able to create statements that are directly related to the <code>serializeTo</code> and <code>deserializeFrom</code>
 * methods of a serializer
 */
public interface SerializeToGenerator {
  /**
   * Creates a statement that is used to store the object (converted as string) to the <code>serializeTo</code> object
   *
   * @param serializerClass the serializer class
   * @param serializeTo     the serialize to var
   * @param objectAsString  the getter invocation used to get the value as string
   * @param fieldInfo       the field info    @return the invocation the serializes the given
   * @return the created statement
   */
  @NotNull
  JStatement createAddToSerializeToExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression serializeTo, @NotNull JExpression objectAsString, @NotNull FieldDeclarationInfo fieldInfo );

  /**
   * Creates an expression that is used to read an object (as String) from <code>deserializeFrom</code>
   *
   * @param serializerClass the serializer class
   * @param deserializeFrom the object that shall be used to deserialize from
   * @param fieldInfo       the field info   @return the expression that returns the object as string
   * @return the created statement
   */
  @NotNull
  JExpression createReadFromDeserializeFromExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull FieldDeclarationInfo fieldInfo );
}
