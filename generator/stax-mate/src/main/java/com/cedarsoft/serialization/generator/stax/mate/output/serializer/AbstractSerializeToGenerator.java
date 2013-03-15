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
import com.cedarsoft.codegen.model.FieldInfo;
import com.cedarsoft.serialization.generator.common.output.serializer.SerializeToGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;

import javax.annotation.Nonnull;

/**
 *
 */
public abstract class AbstractSerializeToGenerator implements SerializeToGenerator {
  @Nonnull
  protected final CodeGenerator codeGenerator;

  protected AbstractSerializeToGenerator( @Nonnull CodeGenerator codeGenerator ) {
    this.codeGenerator = codeGenerator;
  }

  @Nonnull
  protected JFieldVar getConstant( @Nonnull JDefinedClass serializerClass, @Nonnull String constantName, @Nonnull JExpression value ) {
    return codeGenerator.getOrCreateConstant( serializerClass, String.class, constantName, value );
  }

  @Nonnull
  protected JFieldVar getConstant( @Nonnull JDefinedClass serializerClass, @Nonnull FieldInfo fieldInfo ) {
    String constantName = getConstantName( fieldInfo );
    JExpression value = JExpr.lit( fieldInfo.getSimpleName() );
    return getConstant( serializerClass, constantName, value );
  }

  /**
   * Returns the constant name
   *
   * @param fieldInfo the field info
   * @return the constant name
   */
  @Nonnull
  protected String getConstantName( @Nonnull FieldInfo fieldInfo ) {
    return "ELEMENT_" + fieldInfo.getSimpleName().toUpperCase();
  }
}
