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
import com.cedarsoft.codegen.FieldTypeInformation;
import com.cedarsoft.codegen.ParseExpressionFactory;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.serializer.AbstractGenerator;
import com.cedarsoft.serialization.generator.output.serializer.Expressions;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
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
  @Override
  public Expressions createReadFromDeserializeFromExpression( @NotNull AbstractGenerator<?> generator, @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    JExpression readExpression = createReadExpression( serializerClass, deserializeFrom, formatVersion, fieldInfo );

    return new Expressions( codeGenerator.getParseExpressionFactory().createParseExpression( readExpression, fieldInfo ) );
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

  @Override
  public boolean canHandle( @NotNull FieldDeclarationInfo fieldInfo ) {
    return isBuildInType( fieldInfo );
  }

  /**
   * Returns whether the given field info is a build in type
   *
   * @param fieldInfo the field info
   * @return true if the field is of the build in type, false otherwise
   */
  public static boolean isBuildInType( @NotNull FieldTypeInformation fieldInfo ) {
    return ParseExpressionFactory.getSupportedTypeNames().contains( fieldInfo.getType().toString() );
  }

}
