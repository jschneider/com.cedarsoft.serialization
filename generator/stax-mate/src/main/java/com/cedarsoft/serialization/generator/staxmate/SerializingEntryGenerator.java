package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface SerializingEntryGenerator {
  void appendSerializing( @NotNull JMethod method, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldWithInitializationInfo fieldInfo );

  void appendDeserializing( @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldWithInitializationInfo fieldInfo );
}
