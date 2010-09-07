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
import com.cedarsoft.codegen.model.DomainObjectDescriptor;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.codegen.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.serializer.AbstractNamespaceBasedGenerator;
import com.cedarsoft.serialization.generator.output.serializer.AbstractXmlGenerator;
import com.cedarsoft.serialization.generator.output.serializer.SerializeToGenerator;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generator for stax mate based parsers
 */
public class JacksonGenerator extends AbstractNamespaceBasedGenerator {
  @NonNls
  public static final String METHOD_NAME_CLOSE_TAG = "closeTag";

  @NotNull
  private final List<SerializeToGenerator> generators = new ArrayList<SerializeToGenerator>();

  /**
   * Creates a new generator
   *
   * @param codeGenerator the code generator
   */
  public JacksonGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
    generators.add( new AsFieldGenerator( codeGenerator ) );
    generators.add( new ArrayElementGenerator( codeGenerator ) );
    generators.add( new DelegateGenerator( codeGenerator ) );
  }

  @NotNull
  @Override
  protected JMethod createConstructor( @NotNull JDefinedClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    JMethod constructor = serializerClass.constructor( JMod.PUBLIC );
    constructor.body()
      .invoke( METHOD_SUPER ).arg( getNamespace( domainObjectDescriptor.getQualifiedName() ) )
      .arg( createDefaultVersionRangeInvocation( AbstractXmlGenerator.VERSION, AbstractXmlGenerator.VERSION ) );
    return constructor;
  }

  @NotNull
  @Override
  protected Map<FieldWithInitializationInfo, JVar> fillDeSerializationMethods( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
    try {
      return super.fillDeSerializationMethods( domainObjectDescriptor, serializerClass, serializeMethod, deserializeMethod );
    } finally {
      //Call closeTag( deserializeFrom ); on deserialize
      JVar deserializeFrom = deserializeMethod.listParams()[0];
      deserializeMethod.body().directStatement( "//Finally closing element" );
      deserializeMethod.body().invoke( JacksonGenerator.METHOD_NAME_CLOSE_TAG ).arg( deserializeFrom );
    }
  }

  @Override
  @NotNull
  protected JVar appendDeserializeStatement( @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldWithInitializationInfo fieldInfo ) {
    deserializeMethod.body().directStatement( "//" + fieldInfo.getSimpleName() );
    SerializeToGenerator serializeToHandler = getGenerator( fieldInfo );

    Expressions readExpressions = serializeToHandler.createReadFromDeserializeFromExpression( this, serializerClass, deserializeFrom, formatVersion, fieldInfo );

    //Add the (optional) statements before
    for ( JStatement expression : readExpressions.getBefore() ) {
      deserializeMethod.body().add( expression );
    }

    //The field
    JVar field = deserializeMethod.body().decl( serializeToHandler.generateFieldType( fieldInfo ), fieldInfo.getSimpleName(), readExpressions.getExpression() );

    //Add the optional statements after
    for ( JStatement expression : readExpressions.getAfter() ) {
      deserializeMethod.body().add( expression );
    }
    return field;
  }

  @Override
  protected void appendSerializeStatement( @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull JVar formatVersion, @NotNull FieldWithInitializationInfo fieldInfo ) {
    serializeMethod.body().directStatement( "//" + fieldInfo.getSimpleName() );

    SerializeToGenerator serializeToHandler = getGenerator( fieldInfo );
    serializeMethod.body().add( serializeToHandler.createAddToSerializeToExpression( this, serializerClass, serializeTo, fieldInfo, object, formatVersion ) );
  }

  @NotNull
  @Override
  protected JClass createSerializerExtendsExpression( @NotNull JClass domainType ) {
    return codeGenerator.ref( AbstractJacksonSerializer.class ).narrow( domainType );
  }

  @Override
  @NotNull
  protected Class<?> getExceptionType() {
    return JsonProcessingException.class;
  }

  @Override
  @NotNull
  protected Class<?> getSerializeFromType() {
    return JsonParser.class;
  }

  @Override
  @NotNull
  protected Class<?> getSerializeToType() {
    return JsonGenerator.class;
  }

  @NotNull
  protected SerializeToGenerator getGenerator( @NotNull FieldDeclarationInfo fieldInfo ) {
    for ( SerializeToGenerator generator : generators ) {
      if ( generator.canHandle( fieldInfo ) ) {
        return generator;
      }
    }

    throw new IllegalStateException( "No generator found for " + fieldInfo );
  }
}
