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

package com.cedarsoft.serialization.generator.stax.mate.output.serializer;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.common.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.common.output.serializer.AbstractGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

import javax.annotation.Nonnull;

/**
 * Generates a new element
 */
public class AsElementGenerator extends AbstractStringConversionGenerator {

  public static final String METHOD_NAME_GET_CHILD_TEXT = "getChildText";

  public static final String METHOD_NAME_GET_NAMESPACE = "getNamespace";

  public static final String METHOD_NAME_ADD_ELEMENT_WITH_CHARACTERS = "addElementWithCharacters";

  public AsElementGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @Nonnull
  public JInvocation createAddToSerializeToExpression( @Nonnull AbstractGenerator<?> generator, @Nonnull JDefinedClass serializerClass, @Nonnull JExpression serializeTo, @Nonnull FieldDeclarationInfo fieldInfo, @Nonnull JVar object, JVar formatVersion ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    JExpression objectAsString = codeGenerator.getParseExpressionFactory().createToStringExpression( codeGenerator.createGetterInvocation( object, fieldInfo ), fieldInfo );

    return serializeTo.invoke( METHOD_NAME_ADD_ELEMENT_WITH_CHARACTERS )
      .arg( serializeTo.invoke( METHOD_NAME_GET_NAMESPACE ) )
      .arg( constant )
      .arg( objectAsString );
  }

  @Nonnull
  @Override
  public JExpression createReadExpression( @Nonnull JDefinedClass serializerClass, @Nonnull JExpression deserializeFrom, @Nonnull JVar formatVersion, @Nonnull FieldDeclarationInfo fieldInfo ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );
    return JExpr.invoke( METHOD_NAME_GET_CHILD_TEXT ).arg( deserializeFrom ).arg( constant );
  }

  @Override
  public boolean canHandle( @Nonnull FieldDeclarationInfo fieldInfo ) {
    if ( !super.canHandle( fieldInfo ) ) {
      return false;
    }

    return ( ( XmlDecisionCallback ) codeGenerator.getDecisionCallback() ).getSerializationTarget( fieldInfo ) == XmlDecisionCallback.Target.ELEMENT;
  }
}
