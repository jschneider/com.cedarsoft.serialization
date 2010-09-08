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
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.codegen.model.FieldInfo;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.serializer.AbstractGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Generates an attribute
 */
public class AsAttributeGenerator extends AbstractStringConversionGenerator {
  @NonNls
  public static final String METHOD_NAME_ADD_ATTRIBUTE = "addAttribute";
  @NonNls
  public static final String METHOD_NAME_GET_ATTRIBUTE_VALUE = "getAttributeValue";


  public AsAttributeGenerator( @NotNull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @NotNull
  public JInvocation createAddToSerializeToExpression( @NotNull AbstractGenerator<?> generator, @NotNull JDefinedClass serializerClass, @NotNull JExpression serializeTo, @NotNull FieldDeclarationInfo fieldInfo, @NotNull JVar object, JVar formatVersion ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    JExpression objectAsString = codeGenerator.getParseExpressionFactory().createToStringExpression( codeGenerator.createGetterInvocation( object, fieldInfo ), fieldInfo );
    return serializeTo.invoke( METHOD_NAME_ADD_ATTRIBUTE )
      .arg( constant )
      .arg( objectAsString );
  }

  @NotNull
  @Override
  public JExpression createReadExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );
    return deserializeFrom.invoke( METHOD_NAME_GET_ATTRIBUTE_VALUE ).arg( JExpr._null() ).arg( constant );
  }

  /**
   * @noinspection RefusedBequest
   */
  @Override
  @NotNull
  @NonNls
  protected String getConstantName( @NotNull FieldInfo fieldInfo ) {
    return "ATTRIBUTE_" + fieldInfo.getSimpleName().toUpperCase();
  }

  @Override
  public boolean canHandle( @NotNull FieldDeclarationInfo fieldInfo ) {
    if ( !super.canHandle( fieldInfo ) ) {
      return false;
    }

    return ( ( XmlDecisionCallback ) codeGenerator.getDecisionCallback() ).getSerializationTarget( fieldInfo ) == XmlDecisionCallback.Target.ATTRIBUTE;
  }
}
