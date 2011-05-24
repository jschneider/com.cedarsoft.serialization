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

package com.cedarsoft.serialization.generator.output.jackson.serializer;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.Expressions;
import com.cedarsoft.codegen.TypeUtils;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.codegen.model.FieldInfo;
import com.cedarsoft.serialization.generator.output.serializer.AbstractGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * Generates a new element
 */
public class ArrayElementGenerator extends AbstractDelegateGenerator {

  public static final String METHOD_NAME_SERIALIZE = "serializeArray";

  public static final String METHOD_NAME_DESERIALIZE = "deserializeArray";

  public ArrayElementGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @Nonnull
  public JInvocation createAddToSerializeToExpression( @Nonnull AbstractGenerator<?> generator, @Nonnull JDefinedClass serializerClass, @Nonnull JExpression serializeTo, @Nonnull FieldDeclarationInfo fieldInfo, @Nonnull JVar object, JVar formatVersion ) {
    generator.addDelegatingSerializerToConstructor( serializerClass, codeGenerator.ref( TypeUtils.getErasure( fieldInfo.getCollectionParam() ).toString() ) );

    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    JInvocation getterInvocation = codeGenerator.createGetterInvocation( object, fieldInfo );

    return JExpr.invoke( METHOD_NAME_SERIALIZE )
      .arg( getterInvocation )
      .arg( JExpr.dotclass( codeGenerator.ref( fieldInfo.getCollectionParam().toString() ) ) )
      .arg( constant )
      .arg( serializeTo )
      .arg( formatVersion )
      ;
  }

  @Override
  @Nonnull
  public Expressions createReadFromDeserializeFromExpression( @Nonnull AbstractGenerator<?> generator, @Nonnull JDefinedClass serializerClass, @Nonnull JExpression deserializeFrom, @Nonnull JVar formatVersion, @Nonnull FieldDeclarationInfo fieldInfo ) {
    JClass collectionParamType = codeGenerator.ref( fieldInfo.getCollectionParam().toString() );
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    JInvocation expression = JExpr.invoke( METHOD_NAME_DESERIALIZE ).arg( JExpr.dotclass( collectionParamType ) ).arg( constant ).arg( deserializeFrom ).arg( formatVersion );
    return new Expressions( expression );
  }

  @Nonnull
  @Override
  public JClass generateFieldType( @Nonnull FieldDeclarationInfo fieldInfo ) {
    JClass collectionType = codeGenerator.ref( fieldInfo.getCollectionParam().toString() );
    JClass list = codeGenerator.getModel().ref( List.class );
    return list.narrow( collectionType.wildcard() );
  }

  @Override
  public boolean canHandle( @Nonnull FieldDeclarationInfo fieldInfo ) {
    return fieldInfo.isCollectionType();
  }

  @Override
  @Nonnull

  protected String getConstantName( @Nonnull FieldInfo fieldInfo ) {
    return "PROPERTY_" + fieldInfo.getSimpleName().toUpperCase();
  }
}
