package com.cedarsoft.serialization.generator.intellij;

import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

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
   * @param testsTargetDir the target dir for the test classes
   *@param testResourcesTargetDir  the resources target dir
   * @return the generated class
   */
  @Nonnull
  List<? extends PsiClass> generate( @Nonnull SerializerModel serializerModel, @Nonnull PsiDirectory testsTargetDir, @Nonnull PsiDirectory testResourcesTargetDir );
}