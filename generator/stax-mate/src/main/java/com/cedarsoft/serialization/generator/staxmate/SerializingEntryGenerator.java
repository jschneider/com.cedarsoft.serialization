package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface SerializingEntryGenerator {
  void appendSerializing( @NotNull JMethod method, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldDeclarationInfo fieldInfo );

  /**
   * Returns the var the deserialized value is stored in
   *
   * @param method          the method
   * @param deserializeFrom the deserializedFrom
   * @param formatVersion   the format version
   * @param fieldInfo       the field info
   * @return
   */
  @NotNull
  JVar appendDeserializing( @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo );
}
