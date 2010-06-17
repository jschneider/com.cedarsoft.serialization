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

package com.cedarsoft.serialization.generator.output.staxmate.serializer;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractDelegateGenerator extends AbstractSerializeToGenerator {
  @NonNls
  public static final String METHOD_NAME_ADD_ELEMENT = "addElement";
  @NonNls
  public static final String METHOD_NAME_GET_NAMESPACE = "getNamespace";
  @NonNls
  public static final String METHOD_NAME_NEXT_TAG = "nextTag";
  @NonNls
  public static final String METHOD_NAME_CLOSE_TAG = "closeTag";

  protected AbstractDelegateGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @NotNull
  protected JInvocation createCloseTagInvocation( @NotNull JExpression deserializeFrom ) {
    return JExpr.invoke( METHOD_NAME_CLOSE_TAG ).arg( deserializeFrom );
  }

  @NotNull
  protected JInvocation createNextTagInvocation( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull FieldDeclarationInfo fieldInfo ) {
    return JExpr.invoke( METHOD_NAME_NEXT_TAG ).arg( deserializeFrom ).arg( getConstant( serializerClass, fieldInfo ) );
  }

  @NotNull
  protected JInvocation createAddElementExpression( @NotNull JExpression serializeTo, @NotNull JExpression elementName ) {
    return serializeTo.invoke( METHOD_NAME_ADD_ELEMENT ).arg( serializeTo.invoke( METHOD_NAME_GET_NAMESPACE ) ).arg( elementName );
  }
}
