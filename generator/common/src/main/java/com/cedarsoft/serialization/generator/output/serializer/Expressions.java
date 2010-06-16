package com.cedarsoft.serialization.generator.output.serializer;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Expressions {
  /**
   * The expression itself
   */
  @NotNull
  private final JExpression expression;
  @NotNull
  private final List<? extends JStatement> before;
  @NotNull
  private final List<? extends JStatement> after;

  public Expressions( @NotNull JExpression expression ) {
    this( expression, Collections.<JStatement>emptyList(), Collections.<JStatement>emptyList() );
  }

  public Expressions( @NotNull JExpression expression, @NotNull JStatement before, @NotNull JStatement after ) {
    this( expression, Collections.<JStatement>singletonList( before ), Collections.<JStatement>singletonList( after ) );
  }

  public Expressions( @NotNull JExpression expression, @NotNull List<? extends JStatement> before, @NotNull List<? extends JStatement> after ) {
    this.expression = expression;
    this.before = new ArrayList<JStatement>( before );
    this.after = new ArrayList<JStatement>( after );
  }

  @NotNull
  public JExpression getExpression() {
    return expression;
  }

  @NotNull
  public List<? extends JStatement> getBefore() {
    return Collections.unmodifiableList( before );
  }

  @NotNull
  public List<? extends JStatement> getAfter() {
    return Collections.unmodifiableList( after );
  }
}
