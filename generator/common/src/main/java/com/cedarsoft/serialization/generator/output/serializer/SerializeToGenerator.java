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

package com.cedarsoft.serialization.generator.output.serializer;

import com.cedarsoft.codegen.Expressions;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

/**
 * Interface that is able to create statements that are directly related to the <code>serializeTo</code> and <code>deserializeFrom</code>
 * methods of a serializer
 */
public interface SerializeToGenerator {
  /**
   * Creates a statement that is used to store the object (converted as string) to the <code>serializeTo</code> object
   *
   * @param generator       the generator
   * @param serializerClass the serializer class
   * @param serializeTo     the serialize to var
   * @param fieldInfo       the field info    @return the invocation the serializes the given
   * @param object          the object that is serialized     @return the created statement
   */
  @NotNull
  JStatement createAddToSerializeToExpression( @NotNull AbstractGenerator<?> generator, @NotNull JDefinedClass serializerClass, @NotNull JExpression serializeTo, @NotNull FieldDeclarationInfo fieldInfo, @NotNull JVar object );

  /**
   * Creates an expression that is used to read an object (as String) from <code>deserializeFrom</code>
   *
   * @param generator       the generator
   * @param serializerClass the serializer class
   * @param deserializeFrom the object that shall be used to deserialize from
   * @param formatVersion   the format version
   * @param fieldInfo       the field info   @return the expression that returns the object as string  @return the created statement     @return the created expression
   */
  @NotNull
  Expressions createReadFromDeserializeFromExpression( @NotNull AbstractGenerator<?> generator, @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo );

  @NotNull
  JClass generateFieldType( @NotNull FieldDeclarationInfo fieldInfo );

  /**
   * Whether this generator is able to handle the given field
   *
   * @param fieldInfo the field info
   * @return true if this generator is able to handle that field, false otherwise
   */
  boolean canHandle( @NotNull FieldDeclarationInfo fieldInfo );
}
