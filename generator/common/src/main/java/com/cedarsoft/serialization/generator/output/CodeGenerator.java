/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.output.serializer.Decorator;
import com.cedarsoft.serialization.generator.output.serializer.NewInstanceFactory;
import com.cedarsoft.serialization.generator.output.serializer.ParseExpressionFactory;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
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
  private final NewInstanceFactory newInstanceFactory;
  @NotNull
  private final ClassRefSupport classRefSupport;
  @NotNull
  private final T decisionCallback;

  @NotNull
  private final List<Decorator> decorators = new ArrayList<Decorator>();

  public CodeGenerator( @NotNull T decisionCallback ) {
    this( new JCodeModel(), decisionCallback );
  }

  protected CodeGenerator( @NotNull JCodeModel model, @NotNull T decisionCallback ) {
    this.model = model;
    this.classRefSupport = new ClassRefSupport( model );
    this.parseExpressionFactory = new ParseExpressionFactory( model, classRefSupport );
    this.newInstanceFactory = new NewInstanceFactory( model, classRefSupport );
    this.decisionCallback = decisionCallback;
  }

  @NotNull
  public ParseExpressionFactory getParseExpressionFactory() {
    return parseExpressionFactory;
  }

  @NotNull
  public NewInstanceFactory getNewInstanceFactory() {
    return newInstanceFactory;
  }

  @NotNull
  public JCodeModel getModel() {
    return model;
  }

  @NotNull
  public ClassRefSupport getClassRefSupport() {
    return classRefSupport;
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

  @NotNull
  public JClass ref( @NotNull @NonNls String qualifiedName ) {
    return getClassRefSupport().ref( qualifiedName );
  }

  @NotNull
  public JInvocation createGetterInvocation( @NotNull JExpression object, @NotNull FieldDeclarationInfo fieldInfo ) {
    return object.invoke( fieldInfo.getGetterDeclaration().getSimpleName() );
  }

  @NotNull
  public JClass getCollectionTypeClass( @NotNull FieldDeclarationInfo fieldInfo ) {
    return null;
  }
}
