package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NotNull;

/**
 * Interface that is able to create invocations
 */
public interface SerializeToGenerator {
  /**
   * Creates an invocation that is used to store the object (converted as string) to the serializeTo object
   *
   * @param serializeTo    the serialize to var
   * @param objectAsString the getter invocation used to get the value as string
   * @param fieldInfo      the field info
   * @return the invocation the serializes the given
   */
  @NotNull
  JInvocation createAddToSerializeToExpression( @NotNull JExpression serializeTo, @NotNull JExpression objectAsString, @NotNull FieldWithInitializationInfo fieldInfo );

  @NotNull
  JInvocation createReadFromDeserializeFromExpression( @NotNull JExpression deserializeFrom, @NotNull FieldWithInitializationInfo fieldInfo );
}
