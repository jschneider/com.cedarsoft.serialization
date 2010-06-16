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
import com.cedarsoft.serialization.generator.model.FieldTypeInformation;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.serializer.Expressions;
import com.cedarsoft.serialization.generator.output.serializer.ParseExpressionFactory;
import com.cedarsoft.serialization.generator.output.serializer.SerializeToGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SerializingEntryGenerator {
  @NotNull
  private final CodeGenerator<XmlDecisionCallback> codeGenerator;

  @NotNull
  private final SerializeToGenerator asElementGenerator;
  @NotNull
  private final SerializeToGenerator asAttributeGenerator;
  @NotNull
  private final SerializeToGenerator delegateGenerator;
  @NotNull
  private final SerializeToGenerator collectionGenerator;

  public SerializingEntryGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    this.codeGenerator = codeGenerator;
    asAttributeGenerator = new AsAttributeGenerator( codeGenerator );
    asElementGenerator = new AsElementGenerator( codeGenerator );
    collectionGenerator = new CollectionElementGenerator( codeGenerator );
    delegateGenerator = new DelegateGenerator( codeGenerator );
  }

  public void appendSerializing( @NotNull JDefinedClass serializerClass, @NotNull JMethod method, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldDeclarationInfo fieldInfo ) {
    method.body().directStatement( "//" + fieldInfo.getSimpleName() );

    SerializeToGenerator serializeToHandler = getGenerator( fieldInfo );
    method.body().add( serializeToHandler.createAddToSerializeToExpression( serializerClass, serializeTo, fieldInfo, object ) );
  }

  @NotNull
  public JVar appendDeserializing( @NotNull JDefinedClass serializerClass, @NotNull JMethod method, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    method.body().directStatement( "//" + fieldInfo.getSimpleName() );
    SerializeToGenerator serializeToHandler = getGenerator( fieldInfo );

    Expressions readExpressions = serializeToHandler.createReadFromDeserializeFromExpression( serializerClass, deserializeFrom, formatVersion, fieldInfo );

    //Add the (optional) statements before
    for ( JStatement expression : readExpressions.getBefore() ) {
      method.body().add( expression );
    }

    //The field
    JVar field = method.body().decl( serializeToHandler.generateFieldType( fieldInfo ), fieldInfo.getSimpleName(), readExpressions.getExpression() );

    //Add the optional statements after
    for ( JStatement expression : readExpressions.getAfter() ) {
      method.body().add( expression );
    }
    return field;
  }

  @NotNull
  private SerializeToGenerator getGenerator( @NotNull FieldDeclarationInfo fieldInfo ) {
    if ( fieldInfo.isCollectionType() ) {
      return collectionGenerator;
    }

    if ( isBuildInType( fieldInfo ) ) {
      XmlDecisionCallback.Target target = codeGenerator.getDecisionCallback().getSerializationTarget( fieldInfo );
      switch ( target ) {
        case ELEMENT:
          return asElementGenerator;
        case ATTRIBUTE:
          return asAttributeGenerator;
      }

      throw new IllegalStateException( "Should not reach! " + fieldInfo );
    } else {
      return delegateGenerator;
    }
  }

  /**
   * Returns whether the given field info is a build in type
   *
   * @param fieldInfo the field info
   * @return true if the field is of the build in type, false otherwise
   */
  private static boolean isBuildInType( @NotNull FieldTypeInformation fieldInfo ) {
    return ParseExpressionFactory.getSupportedTypeNames().contains( fieldInfo.getType().toString() );
  }
}
