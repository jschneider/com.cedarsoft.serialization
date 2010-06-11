package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NotNull;

/**
 * @param <T> the type of the decision callback
 */
public class CodeGenerator<T extends DecisionCallback> {
  @NotNull
  protected final JCodeModel model;
  @NotNull
  private final ParseExpressionFactory parseExpressionFactory;

  @NotNull
  private final T decisionCallback;

  public CodeGenerator( @NotNull T decisionCallback ) {
    this( new JCodeModel(), decisionCallback );
  }

  public CodeGenerator( @NotNull JCodeModel model, @NotNull T decisionCallback ) {
    this.model = model;
    this.parseExpressionFactory = new ParseExpressionFactory( model );
    this.decisionCallback = decisionCallback;
  }

  @NotNull
  public ParseExpressionFactory getParseExpressionFactory() {
    return parseExpressionFactory;
  }

  @NotNull
  public JCodeModel getModel() {
    return model;
  }

  @NotNull
  public T getDecisionCallback() {
    return decisionCallback;
  }
}
