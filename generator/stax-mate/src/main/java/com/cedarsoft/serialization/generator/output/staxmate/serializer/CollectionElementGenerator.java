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
import com.cedarsoft.serialization.generator.output.serializer.SerializeToGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generates a new element
 */
public class CollectionElementGenerator extends AbstractSerializeToGenerator {
  @NonNls
  public static final String METHOD_NAME_SERIALIZE_COLLECTION = "serializeCollection";
  @NonNls
  public static final String METHOD_NAME_DESERIALIZE_COLLECTION = "deserializeCollection";

  public CollectionElementGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @NotNull
  public JInvocation createAddToSerializeToExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression serializeTo, @NotNull FieldDeclarationInfo fieldInfo, @NotNull JVar object ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    JInvocation getterInvocation = codeGenerator.createGetterInvocation( object, fieldInfo );
    JClass collectionType = codeGenerator.ref( fieldInfo.getCollectionType().toString() );

    return JExpr.invoke( METHOD_NAME_SERIALIZE_COLLECTION )
      .arg( getterInvocation )
      .arg( JExpr.dotclass( collectionType ) )
      .arg( constant )
      .arg( serializeTo )
      ;
  }

  @Override
  @NotNull
  public JInvocation createReadFromDeserializeFromExpression( @NotNull JDefinedClass serializerClass, @NotNull JExpression deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    JClass collectionType = codeGenerator.ref( fieldInfo.getCollectionType().toString() );

//    JFieldVar constant = getConstant( serializerClass, fieldInfo );
    return JExpr.invoke( METHOD_NAME_DESERIALIZE_COLLECTION ).arg( deserializeFrom ).arg( JExpr.dotclass( collectionType ) ).arg( formatVersion );
  }

  @NotNull
  @Override
  public JClass generateFieldType( @NotNull FieldDeclarationInfo fieldInfo ) {
    JClass collectionType = codeGenerator.ref( fieldInfo.getCollectionType().toString() );
    JClass list = codeGenerator.getModel().ref( List.class );
    return list.narrow( collectionType.wildcard() );
  }

  @NotNull
  private JFieldVar getConstant( @NotNull JDefinedClass serializerClass, @NotNull FieldInfo fieldInfo ) {
    return codeGenerator.getOrCreateConstant( serializerClass, String.class, getConstantName( fieldInfo ), JExpr.lit( fieldInfo.getSimpleName() ) );
  }

  @NotNull
  @NonNls
  protected String getConstantName( @NotNull FieldInfo fieldInfo ) {
    return "ELEMENT_" + fieldInfo.getSimpleName().toUpperCase();
  }
}
