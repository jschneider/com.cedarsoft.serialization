package com.cedarsoft.serialization.generator.decision;

import com.cedarsoft.serialization.generator.model.FieldInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 */
public class DefaultXmlDecisionCallback implements XmlDecisionCallback {
  @NotNull
  @NonNls
  private final Collection<String> fieldsAsAttribute = new HashSet<String>();

  public DefaultXmlDecisionCallback( @NotNull @NonNls String... fieldsAsAttribute ) {
    this( Arrays.asList( fieldsAsAttribute ) );
  }

  public DefaultXmlDecisionCallback( @NotNull @NonNls Collection<? extends String> fieldsAsAttribute ) {
    this.fieldsAsAttribute.addAll( fieldsAsAttribute );
  }

  @NotNull
  @Override
  public Target getSerializationTarget( @NotNull FieldInfo fieldInfo ) {
    if ( fieldsAsAttribute.contains( fieldInfo.getSimpleName() ) ) {
      return Target.ATTRIBUTE;
    }
    return Target.ELEMENT;
  }
}
