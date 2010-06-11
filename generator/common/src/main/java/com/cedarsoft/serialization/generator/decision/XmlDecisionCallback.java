package com.cedarsoft.serialization.generator.decision;

import com.cedarsoft.serialization.generator.model.FieldInfo;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface XmlDecisionCallback extends DecisionCallback {
  /**
   * Returns the serialization target for the given field
   *
   * @param fieldInfo the field info
   * @return the serialization target
   */
  @NotNull
  Target getSerializationTarget( @NotNull FieldInfo fieldInfo );

  enum Target {
    ELEMENT,
    ATTRIBUTE
  }
}



