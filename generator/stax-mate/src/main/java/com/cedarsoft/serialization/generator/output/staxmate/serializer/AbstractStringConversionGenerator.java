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

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.model.FieldInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractStringConversionGenerator extends AbstractSerializeToGenerator {
  protected AbstractStringConversionGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @NotNull
  @Override
  public JClass generateFieldType( @NotNull FieldDeclarationInfo fieldInfo ) {
    return codeGenerator.ref( fieldInfo.getType().toString() );
  }

  @NotNull
  protected JFieldVar getConstant( @NotNull JDefinedClass serializerClass, @NotNull FieldInfo fieldInfo ) {
    String constantName = getConstantName( fieldInfo );
    JExpression value = JExpr.lit( fieldInfo.getSimpleName() );
    return getConstant( serializerClass, constantName, value );
  }

  @NotNull
  @Override
  public JExpression createReadFromDeserializeFromExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    JExpression readExpression = createReadExpression( serializerClass, deserializeFrom, formatVersion, fieldInfo );

    return codeGenerator.getParseExpressionFactory().createParseExpression( readExpression, fieldInfo );
  }

  /**
   * Creates the read expression (without conversion to string)
   *
   * @param serializerClass the serializer class
   * @param deserializeFrom the deserialize from
   * @param formatVersion   the format version
   * @param fieldInfo       the field info
   * @return the expression to read the value
   */
  @NotNull
  public abstract JExpression createReadExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo );

  /**
   * Returns the constant name
   *
   * @param fieldInfo the field info
   * @return the constant name
   */
  @NotNull
  @NonNls
  protected abstract String getConstantName( @NotNull FieldInfo fieldInfo );
}
