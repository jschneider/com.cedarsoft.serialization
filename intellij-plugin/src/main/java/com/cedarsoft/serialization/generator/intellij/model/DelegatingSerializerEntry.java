package com.cedarsoft.serialization.generator.intellij.model;

import com.intellij.psi.PsiType;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class DelegatingSerializerEntry {
  @Nonnull
  private final PsiType serializedType;
  @Nonnull
  private final PsiType delegatingSerializerType;
  @Nonnull
  private final String serializerParamName;

  public DelegatingSerializerEntry( @Nonnull PsiType serializedType, @Nonnull PsiType delegatingSerializerType, @Nonnull String serializerParamName ) {
    this.serializedType = serializedType;
    this.delegatingSerializerType = delegatingSerializerType;
    this.serializerParamName = serializerParamName;
  }

  @Nonnull
  public PsiType getDelegatingSerializerType() {
    return delegatingSerializerType;
  }

  @Nonnull
  public PsiType getSerializedType() {
    return serializedType;
  }

  @Nonnull
  public String getSerializerParamName() {
    return serializerParamName;
  }

}
