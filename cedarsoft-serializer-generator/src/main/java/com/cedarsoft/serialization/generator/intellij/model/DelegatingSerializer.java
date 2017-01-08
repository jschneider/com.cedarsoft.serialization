package com.cedarsoft.serialization.generator.intellij.model;

import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class DelegatingSerializer {
  @Nonnull
  private final PsiType serializedType;
  @Nonnull
  private final PsiType delegatingSerializerType;
  @Nonnull
  private final String serializerParamName;

  public DelegatingSerializer( @Nonnull PsiType serializedType, @Nonnull PsiType delegatingSerializerType, @Nonnull String serializerParamName ) {
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
  public String getSerializedTypeBoxed() {
    return box( getSerializedType() );
  }

  @Nonnull
  public String getSerializerParamName() {
    return serializerParamName;
  }

  @Nonnull
  public static String box( @Nonnull PsiType type ) {
    if ( type instanceof PsiPrimitiveType ) {
      return ( ( PsiPrimitiveType ) type ).getBoxedTypeName();
    }

    return type.getCanonicalText();
  }
}
