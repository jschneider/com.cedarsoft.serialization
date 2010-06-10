package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface SerializingEntryGenerator {
  void appendSerializing( @NotNull JCodeModel model, @NotNull JMethod serializeMethod, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldWithInitializationInfo fieldInfo );
}
