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

package com.cedarsoft.serialization.generator.jackson.output.serializer;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.Expressions;
import com.cedarsoft.codegen.TypeUtils;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.codegen.model.FieldInfo;
import com.cedarsoft.serialization.generator.common.output.serializer.AbstractGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Generates an attribute
 */
public class AsFieldGenerator extends AbstractSerializeToGenerator {
  public AsFieldGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @Nonnull
  public JInvocation createAddToSerializeToExpression( @Nonnull AbstractGenerator<?> generator, @Nonnull JDefinedClass serializerClass, @Nonnull JExpression serializeTo, @Nonnull FieldDeclarationInfo fieldInfo, @Nonnull JVar object, JVar formatVersion ) {
    JFieldVar constant = getConstant( serializerClass, fieldInfo );

    JInvocation getter = codeGenerator.createGetterInvocation( object, fieldInfo );

    if ( fieldInfo.isType( String.class ) ) {
      return serializeTo.invoke( "writeStringField" )
        .arg( constant )
        .arg( getter );
    }

    if ( TypeUtils.isNumberType( fieldInfo.getType() ) ) {
      return serializeTo.invoke( "writeNumberField" )
        .arg( constant )
        .arg( getter );
    }

    if ( TypeUtils.isBooleanType( fieldInfo.getType() ) ) {
      return serializeTo.invoke( "writeBooleanField" )
        .arg( constant )
        .arg( getter );
    }

    throw new IllegalArgumentException( "Invalid field: " + fieldInfo );
  }

  @Nonnull
  @Override
  public JClass generateFieldType( @Nonnull FieldDeclarationInfo fieldInfo ) {
    return codeGenerator.ref( fieldInfo.getType().toString() );
  }

  @Override
  public boolean canHandle( @Nonnull FieldDeclarationInfo fieldInfo ) {
    return TypeUtils.isSimpleType( fieldInfo.getType() );
  }

  @Nonnull
  @Override
  public Expressions createReadFromDeserializeFromExpression( @Nonnull AbstractGenerator<?> generator, @Nonnull JDefinedClass serializerClass, @Nonnull JExpression deserializeFrom, @Nullable JVar wrapper, @Nonnull JVar formatVersion, @Nonnull FieldDeclarationInfo fieldInfo ) {
    assert wrapper != null;

    JFieldVar constant = getConstant( serializerClass, fieldInfo );
    JStatement nextFieldStatement = wrapper.invoke( "nextFieldValue" ).arg( constant );

    JExpression readExpression = createReadExpression( serializerClass, deserializeFrom, formatVersion, fieldInfo );
    return new Expressions( readExpression, nextFieldStatement );
  }

  @Nonnull
  public JExpression createReadExpression( @Nonnull JDefinedClass serializerClass, @Nonnull JExpression deserializeFrom, @Nonnull JVar formatVersion, @Nonnull FieldDeclarationInfo fieldInfo ) {
    if ( fieldInfo.isType( String.class ) ) {
      return deserializeFrom.invoke( "getText" );
    }

    if ( fieldInfo.isType( Integer.TYPE ) || fieldInfo.isType( Integer.class ) ) {
      return deserializeFrom.invoke( "getIntValue" );
    }

    if ( fieldInfo.isType( Double.TYPE ) || fieldInfo.isType( Double.class ) ) {
      return deserializeFrom.invoke( "getDoubleValue" );
    }

    if ( fieldInfo.isType( Boolean.TYPE ) || fieldInfo.isType( Boolean.class ) ) {
      return deserializeFrom.invoke( "getBooleanValue" );
    }

    if ( fieldInfo.isType( Float.TYPE ) || fieldInfo.isType( Float.class ) ) {
      return deserializeFrom.invoke( "getFloatValue" );
    }

    if ( fieldInfo.isType( Long.TYPE ) || fieldInfo.isType( Long.class ) ) {
      return deserializeFrom.invoke( "getLongValue" );
    }

    throw new IllegalArgumentException( "Invalid field info " + fieldInfo );
  }

  /**
   * @noinspection RefusedBequest
   */
  @Override
  @Nonnull

  protected String getConstantName( @Nonnull FieldInfo fieldInfo ) {
    return "PROPERTY_" + fieldInfo.getSimpleName().toUpperCase();
  }

}
