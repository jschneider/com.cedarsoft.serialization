package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  @NotNull
  private final List<Decorator> decorators = new ArrayList<Decorator>();

  public CodeGenerator( @NotNull T decisionCallback ) {
    this( new JCodeModel(), decisionCallback );
  }

  protected CodeGenerator( @NotNull JCodeModel model, @NotNull T decisionCallback ) {
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

  public void addMethodDecorator( @NotNull Decorator decorator ) {
    this.decorators.add( decorator );
  }

  @NotNull
  public List<? extends Decorator> getMethodDecorators() {
    return Collections.unmodifiableList( decorators );
  }

  @NotNull
  public JFieldVar getOrCreateConstant( @NotNull JDefinedClass serializerClass, @NotNull Class<?> type, @NotNull @NonNls String constantName, @NotNull JExpression initExpression ) {
    //Get the constant if it still exists
    JFieldVar fieldVar = serializerClass.fields().get( constantName );
    if ( fieldVar != null ) {
      return fieldVar;
    }

    //Create
    return createConstant( serializerClass, type, constantName, initExpression );
  }

  @NotNull
  public JFieldVar createConstant( @NotNull JDefinedClass serializerClass, @NotNull Class<?> type, @NotNull @NonNls String constantName, @NotNull JExpression initExpression ) {
    JFieldVar constant = serializerClass.field( JMod.FINAL | JMod.PUBLIC | JMod.STATIC, type, constantName, initExpression );

    for ( Decorator decorator : decorators ) {
      decorator.decorateConstant( this, constant );
    }

    return constant;
  }
}
