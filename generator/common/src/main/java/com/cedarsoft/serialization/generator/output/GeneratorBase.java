package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all types of generators
 *
 * @param <T> the type of the decision callback
 */
public abstract class GeneratorBase<T extends DecisionCallback> {
  @NotNull
  protected final CodeGenerator<T> codeGenerator;
  @NotNull
  protected final JCodeModel codeModel;

  protected GeneratorBase( @NotNull CodeGenerator<T> codeGenerator ) {
    this.codeGenerator = codeGenerator;
    this.codeModel = codeGenerator.getModel();
  }

  @NotNull
  public final CodeGenerator<T> getCodeGenerator() {
    return codeGenerator;
  }

  @NotNull
  public final JCodeModel getCodeModel() {
    return codeModel;
  }
}
