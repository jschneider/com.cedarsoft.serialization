package com.cedarsoft.serialization.generator.intellij;

import com.intellij.psi.PsiType;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface SerializerResolver {
  @Nonnull
  PsiType findSerializerFor( @Nonnull PsiType typeToSerialize );

  @Nonnull
  String guessSerializerName( @Nonnull PsiType typeToSerialize );
}