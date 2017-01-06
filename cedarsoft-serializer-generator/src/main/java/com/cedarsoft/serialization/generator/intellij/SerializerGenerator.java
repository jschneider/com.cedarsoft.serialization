package com.cedarsoft.serialization.generator.intellij;

import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

import javax.annotation.Nonnull;

/**
 * Generates a serialize
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface SerializerGenerator {
  /**
   * Generates a serializer for the given model
   *
   * @param serializerModel the serializer model
   * @return the generated serializer
   */
  @Nonnull
  PsiClass generate( @Nonnull SerializerModel serializerModel, @Nonnull PsiDirectory targetDir );
}