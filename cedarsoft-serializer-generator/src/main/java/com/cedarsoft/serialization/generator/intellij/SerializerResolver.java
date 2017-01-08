package com.cedarsoft.serialization.generator.intellij;

import com.intellij.psi.PsiType;

import javax.annotation.Nonnull;

/**
 * Resolver for serializers for a given type.
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface SerializerResolver {
  /**
   * Returns the serializer for a given type
   *
   * @param typeToSerialize the type serializer
   * @return the serializer type
   */
  @Nonnull
  PsiType findSerializerFor( @Nonnull PsiType typeToSerialize );

  /**
   * Guesses the serializer name
   *
   * @param typeToSerialize the type to serialize
   * @return the serializer name
   */
  @Nonnull
  String guessSerializerName( @Nonnull PsiType typeToSerialize );
}