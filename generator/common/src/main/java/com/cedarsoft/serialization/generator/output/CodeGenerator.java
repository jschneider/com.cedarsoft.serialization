package com.cedarsoft.serialization.generator.output;

import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class CodeGenerator {
  @NotNull
  protected final JCodeModel model;
  @NotNull
  private final ParseExpressionFactory parseExpressionFactory;

  public CodeGenerator( @NotNull JCodeModel model, @NotNull ParseExpressionFactory parseExpressionFactory ) {
    this.model = model;
    this.parseExpressionFactory = parseExpressionFactory;
  }

  @NotNull
  public ParseExpressionFactory getParseExpressionFactory() {
    return parseExpressionFactory;
  }

  @NotNull
  public JCodeModel getModel() {
    return model;
  }
}
