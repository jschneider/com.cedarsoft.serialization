package com.cedarsoft.serialization.generator.intellij;

import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.intellij.psi.PsiClass;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Generates a serializer test
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface SerializerTestsGenerator {
  /**
   * Generates the serializer for the given serializer model
   *
   * @param serializerModel the serializer model
   * @return the generated class
   */
  @Nonnull
  List<? extends PsiClass> generate( @Nonnull SerializerModel serializerModel );
}